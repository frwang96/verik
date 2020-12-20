/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package verikc.kt.parse

import verikc.al.AlRule
import verikc.al.AlRuleType
import verikc.al.AlTokenType
import verikc.base.symbol.SymbolContext
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_FOR_INDICES
import verikc.lang.LangSymbol.OPERATOR_WITH

object KtParserExpression {

    fun parse(expression: AlRule, symbolContext: SymbolContext): KtExpression {
        return parseDisjunction(expression.childAs(AlRuleType.DISJUNCTION), symbolContext)
    }

    private fun reduceRight(
        root: AlRule,
        map: (AlRule) -> KtExpression,
        acc: (KtExpression, KtExpression) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        var x = map(root.children[0].asRule())
        for (child in root.children.drop(1)) {
            x = acc(x, map(child.asRule()))
        }
        return x
    }

    private fun reduceOp(
        root: AlRule,
        map: (AlRule) -> KtExpression,
        acc: (KtExpression, KtExpression, AlRule) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        val iterator = root.children.iterator()
        var x = map(iterator.next().asRule())
        while (iterator.hasNext()) {
            val op = iterator.next().asRule()
            if (!iterator.hasNext()) {
                throw LineException("expression expected", root.line)
            }
            val y = map(iterator.next().asRule())
            x = acc(x, y, op)
        }
        return x
    }

    private fun parseDisjunction(disjunction: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceRight(disjunction, { parseConjunction(it, symbolContext) }) { x, y ->
            KtExpressionFunction(
                disjunction.line,
                null,
                "||",
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseConjunction(conjunction: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceRight(conjunction, { parseEquality(it, symbolContext) }) { x, y ->
            KtExpressionFunction(
                conjunction.line,
                null,
                "&&",
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseEquality(equality: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceOp(equality, { parseComparison(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.EXCL_EQ -> "!="
                AlTokenType.EQEQ -> "=="
                else -> throw LineException("equality operator expected", equality.line)
            }
            KtExpressionFunction(
                equality.line,
                null,
                identifier,
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseComparison(comparison: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceOp(comparison, { parseInfixOperation(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.LANGLE -> "<"
                AlTokenType.RANGLE -> ">"
                AlTokenType.LE -> "<="
                AlTokenType.GE -> ">="
                else -> throw LineException("comparison operator expected", comparison.line)
            }
            KtExpressionFunction(
                comparison.line,
                null,
                identifier,
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseInfixOperation(infixOperation: AlRule, symbolContext: SymbolContext): KtExpression {
        return if (infixOperation.containsType(AlRuleType.IS_OPERATOR)) {
            if (infixOperation.children.size != 3) {
                throw LineException("unable to parse is expression", infixOperation.line)
            }
            val type = KtParserTypeIdentifier.parse(infixOperation.childAs(AlRuleType.TYPE))
            val typeExpression = KtExpressionProperty(
                infixOperation.line,
                null,
                type,
                null,
                null
            )
            val identifier = when (infixOperation.childAs(AlRuleType.IS_OPERATOR).firstAsTokenType()) {
                AlTokenType.IS -> "is"
                AlTokenType.NOT_IS -> "!is"
                else -> throw LineException("is operator expected", infixOperation.line)
            }
            KtExpressionFunction(
                infixOperation.line,
                null,
                identifier,
                parseInfixFunctionCall(infixOperation.firstAsRule().firstAsRule(), symbolContext),
                listOf(typeExpression),
                null
            )
        } else {
            reduceOp(infixOperation, { parseInfixFunctionCall(it.firstAsRule(), symbolContext) }) { x, y, op ->
                val identifier = when (op.firstAsTokenType()) {
                    AlTokenType.IN -> "in"
                    AlTokenType.NOT_IN -> "!in"
                    else -> throw LineException("infix operator expected", infixOperation.line)
                }
                KtExpressionFunction(
                    infixOperation.line,
                    null,
                    identifier,
                    x,
                    listOf(y),
                    null
                )
            }
        }
    }

    private fun parseInfixFunctionCall(infixFunctionCall: AlRule, symbolContext: SymbolContext): KtExpression {
        if (infixFunctionCall.children.isEmpty()) {
            throw LineException("rule node has no children", infixFunctionCall.line)
        }
        val iterator = infixFunctionCall.children.iterator()
        var expression = parseRangeExpression(iterator.next().asRule(), symbolContext)
        while (iterator.hasNext()) {
            val identifier = iterator
                .next()
                .asRule()
                .firstAsTokenText()

            if (!iterator.hasNext()) {
                throw LineException("expression expected", infixFunctionCall.line)
            }
            val argOrBlock = iterator.next().asRule()
            val block = parseInfixFunctionCallBlock(argOrBlock, symbolContext)
            if (block != null) {
                val (operator, lambdaPropertyCount) =
                    parseInfixFunctionCallOperator(identifier, infixFunctionCall.line)
                val augmentedBlock = if (lambdaPropertyCount == 1) {
                    when (block.lambdaProperties.size) {
                        0 -> {
                            val lambdaProperty = KtLambdaProperty(
                                infixFunctionCall.line,
                                "it",
                                symbolContext.registerSymbol("it"),
                                null
                            )
                            KtBlock(
                                block.line,
                                block.symbol,
                                listOf(lambdaProperty),
                                block.statements
                            )
                        }
                        1 -> block
                        else -> throw LineException("wrong number of lambda parameters", infixFunctionCall.line)
                    }
                } else {
                    if (block.lambdaProperties.size == lambdaPropertyCount) block
                    else throw LineException("wrong number of lambda parameters", infixFunctionCall.line)
                }

                expression = KtExpressionOperator(
                    infixFunctionCall.line,
                    null,
                    operator,
                    expression,
                    listOf(),
                    listOf(augmentedBlock)
                )
            } else {
                val arg = parseRangeExpression(argOrBlock, symbolContext)
                expression = KtExpressionFunction(
                    infixFunctionCall.line,
                    null,
                    identifier,
                    expression,
                    listOf(arg),
                    null
                )
            }
        }
        return expression
    }

    private fun parseInfixFunctionCallBlock(rangeExpression: AlRule, symbolContext: SymbolContext): KtBlock? {
        val primaryExpression = rangeExpression
            .let { if (it.children.size == 1) it.childAs(AlRuleType.ADDITIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.childAs(AlRuleType.MULTIPLICATIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.childAs(AlRuleType.AS_EXPRESSION) else null }
            ?.let {
                if (it.firstAsRule().children.size == 1) it.firstAsRule()
                    .childAs(AlRuleType.PREFIX_UNARY_EXPRESSION)
                else null
            }
            ?.let { if (it.children.size == 1) it.childAs(AlRuleType.POSTFIX_UNARY_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.childAs(AlRuleType.PRIMARY_EXPRESSION) else null }
        return if (primaryExpression != null && primaryExpression.containsType(AlRuleType.FUNCTION_LITERAL)) {
            return primaryExpression
                .childAs(AlRuleType.FUNCTION_LITERAL)
                .childAs(AlRuleType.LAMBDA_LITERAL)
                .let { KtParserBlock.parseLambdaLiteral(it, symbolContext) }
        } else null
    }

    private fun parseInfixFunctionCallOperator(identifier: String, line: Line): Pair<Symbol, Int> {
        return when (identifier) {
            "with" -> Pair(OPERATOR_WITH, 1)
            "for_each" -> Pair(OPERATOR_FOR_EACH, 1)
            "for_indices" -> Pair(OPERATOR_FOR_INDICES, 1)
            else -> throw LineException("infix operator $identifier not supported", line)
        }
    }

    private fun parseRangeExpression(rangeExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceRight(rangeExpression, { parseAdditiveExpression(it, symbolContext) }) { x, y ->
            KtExpressionFunction(
                rangeExpression.line,
                null,
                "..",
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseAdditiveExpression(additiveExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceOp(additiveExpression, { parseMultiplicativeExpression(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.ADD -> "+"
                AlTokenType.SUB -> "-"
                else -> throw LineException("additive operator expected", additiveExpression.line)
            }
            KtExpressionFunction(
                additiveExpression.line,
                null,
                identifier,
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        return reduceOp(multiplicativeExpression, { parseAsExpression(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.firstAsTokenType()) {
                AlTokenType.MULT -> "*"
                AlTokenType.MOD -> "%"
                AlTokenType.DIV -> "/"
                else -> throw LineException("multiplicative operator expected", multiplicativeExpression.line)
            }
            KtExpressionFunction(
                multiplicativeExpression.line,
                null,
                identifier,
                x,
                listOf(y),
                null
            )
        }
    }

    private fun parseAsExpression(asExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        return if (asExpression.containsType(AlRuleType.AS_OPERATOR)) {
            val type = KtParserTypeIdentifier.parse(asExpression.childAs(AlRuleType.TYPE))
            val typeExpression = KtExpressionProperty(
                asExpression.line,
                null,
                type,
                null,
                null
            )
            KtExpressionFunction(
                asExpression.line,
                null,
                "as",
                KtParserExpressionUnary.parsePrefixUnaryExpression(
                    asExpression.firstAsRule().firstAsRule(),
                    symbolContext
                ),
                listOf(typeExpression),
                null
            )
        } else {
            KtParserExpressionUnary.parsePrefixUnaryExpression(asExpression.firstAsRule().firstAsRule(), symbolContext)
        }
    }
}
