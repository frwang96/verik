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

import verikc.alx.AlxRuleIndex
import verikc.alx.AlxTerminalIndex
import verikc.alx.AlxTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_FOR_INDICES
import verikc.lang.LangSymbol.OPERATOR_WITH

object KtParserExpression {

    fun parse(expression: AlxTree, symbolContext: SymbolContext): KtExpression {
        return parseDisjunction(expression.find(AlxRuleIndex.DISJUNCTION), symbolContext)
    }

    private fun reduceOp(
        root: AlxTree,
        map: (AlxTree) -> KtExpression,
        acc: (KtExpression, KtExpression, AlxTree) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        val iterator = root.children.iterator()
        var x = map(iterator.next())
        while (iterator.hasNext()) {
            val op = iterator.next()
            if (!iterator.hasNext()) {
                throw LineException("expression expected", root.line)
            }
            val y = map(iterator.next())
            x = acc(x, y, op)
        }
        return x
    }

    private fun parseDisjunction(disjunction: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(disjunction, { parseConjunction(it, symbolContext) }) { x, y, _ ->
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

    private fun parseConjunction(conjunction: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(conjunction, { parseEquality(it, symbolContext) }) { x, y, _ ->
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

    private fun parseEquality(equality: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(equality, { parseComparison(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlxTerminalIndex.EXCL_EQ -> "!="
                AlxTerminalIndex.EQEQ -> "=="
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

    private fun parseComparison(comparison: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(comparison, { parseInfixOperation(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlxTerminalIndex.LANGLE -> "<"
                AlxTerminalIndex.RANGLE -> ">"
                AlxTerminalIndex.LE -> "<="
                AlxTerminalIndex.GE -> ">="
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

    private fun parseInfixOperation(infixOperation: AlxTree, symbolContext: SymbolContext): KtExpression {
        return if (infixOperation.contains(AlxRuleIndex.IS_OPERATOR)) {
            if (infixOperation.children.size != 3) {
                throw LineException("unable to parse is expression", infixOperation.line)
            }
            val type = KtParserTypeIdentifier.parse(infixOperation.find(AlxRuleIndex.TYPE))
            val typeExpression = KtExpressionProperty(
                infixOperation.line,
                null,
                type,
                null,
                null
            )
            val identifier = when (infixOperation.find(AlxRuleIndex.IS_OPERATOR).unwrap().index) {
                AlxTerminalIndex.IS -> "is"
                AlxTerminalIndex.NOT_IS -> "!is"
                else -> throw LineException("is operator expected", infixOperation.line)
            }
            val infixFunctionCall = infixOperation
                .find(AlxRuleIndex.ELVIS_EXPRESSION)
                .find(AlxRuleIndex.INFIX_FUNCTION_CALL)
            KtExpressionFunction(
                infixOperation.line,
                null,
                identifier,
                parseInfixFunctionCall(infixFunctionCall, symbolContext),
                listOf(typeExpression),
                null
            )
        } else {
            reduceOp(
                infixOperation,
                { parseInfixFunctionCall(it.find(AlxRuleIndex.INFIX_FUNCTION_CALL), symbolContext) }
            ) { x, y, op ->
                val identifier = when (op.unwrap().index) {
                    AlxTerminalIndex.IN -> "in"
                    AlxTerminalIndex.NOT_IN -> "!in"
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

    private fun parseInfixFunctionCall(infixFunctionCall: AlxTree, symbolContext: SymbolContext): KtExpression {
        if (infixFunctionCall.children.isEmpty()) {
            throw LineException("rule node has no children", infixFunctionCall.line)
        }
        val iterator = infixFunctionCall.children.iterator()
        var expression = parseRangeExpression(iterator.next(), symbolContext)
        while (iterator.hasNext()) {
            val identifier = iterator
                .next()
                .find(AlxTerminalIndex.IDENTIFIER).text!!

            if (!iterator.hasNext()) {
                throw LineException("expression expected", infixFunctionCall.line)
            }
            val argOrBlock = iterator.next()
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

    private fun parseInfixFunctionCallBlock(rangeExpression: AlxTree, symbolContext: SymbolContext): KtBlock? {
        val primaryExpression = rangeExpression
            .let { if (it.children.size == 1) it.find(AlxRuleIndex.ADDITIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.MULTIPLICATIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.AS_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.COMPARISON_WITH_LITERAL_RIGHT_SIDE) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.PREFIX_UNARY_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.POSTFIX_UNARY_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlxRuleIndex.PRIMARY_EXPRESSION) else null }
        return if (primaryExpression != null && primaryExpression.contains(AlxRuleIndex.FUNCTION_LITERAL)) {
            return primaryExpression
                .find(AlxRuleIndex.FUNCTION_LITERAL)
                .find(AlxRuleIndex.LAMBDA_LITERAL)
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

    private fun parseRangeExpression(rangeExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(rangeExpression, { parseAdditiveExpression(it, symbolContext) }) { x, y, _ ->
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

    private fun parseAdditiveExpression(additiveExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(additiveExpression, { parseMultiplicativeExpression(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlxTerminalIndex.ADD -> "+"
                AlxTerminalIndex.SUB -> "-"
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

    private fun parseMultiplicativeExpression(multiplicativeExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        return reduceOp(multiplicativeExpression, { parseAsExpression(it, symbolContext) }) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlxTerminalIndex.MULT -> "*"
                AlxTerminalIndex.MOD -> "%"
                AlxTerminalIndex.DIV -> "/"
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

    private fun parseAsExpression(asExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        val prefixUnaryExpression = asExpression
            .find(AlxRuleIndex.COMPARISON_WITH_LITERAL_RIGHT_SIDE)
            .find(AlxRuleIndex.PREFIX_UNARY_EXPRESSION)
        return if (asExpression.contains(AlxRuleIndex.AS_OPERATOR)) {
            val type = KtParserTypeIdentifier.parse(asExpression.find(AlxRuleIndex.TYPE))
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
                KtParserExpressionUnary.parsePrefixUnaryExpression(prefixUnaryExpression, symbolContext),
                listOf(typeExpression),
                null
            )
        } else {
            KtParserExpressionUnary.parsePrefixUnaryExpression(prefixUnaryExpression, symbolContext)
        }
    }
}
