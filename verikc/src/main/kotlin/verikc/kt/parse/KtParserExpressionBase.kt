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

import verikc.al.ast.AlRule
import verikc.al.ast.AlTerminal
import verikc.al.ast.AlTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_FOR_EACH
import verikc.lang.LangSymbol.OPERATOR_FOR_INDICES
import verikc.lang.LangSymbol.OPERATOR_WITH

object KtParserExpressionBase {

    fun parse(expression: AlTree, symbolContext: SymbolContext): KtExpression {
        return parseDisjunction(expression.find(AlRule.DISJUNCTION), symbolContext)
    }

    private fun reduceBinaryOperator(
        root: AlTree,
        expressionIndex: Int,
        operatorIndex: Int,
        map: (AlTree) -> KtExpression,
        acc: (KtExpression, KtExpression, AlTree) -> KtExpression
    ): KtExpression {
        if (root.children.isEmpty()) {
            throw LineException("rule node has no children", root.line)
        }
        val iterator = root.children.iterator()

        var expression: AlTree
        do { expression = iterator.next() } while (expression.index != expressionIndex)
        var mappedExpression = map(expression)

        while (iterator.hasNext()) {
            var operator: AlTree
            do { operator = iterator.next() } while (operator.index != operatorIndex)
            var nextExpression: AlTree
            do { nextExpression = iterator.next() } while (nextExpression.index != expressionIndex)
            mappedExpression = acc(mappedExpression, map(nextExpression), operator)
        }
        return mappedExpression
    }

    private fun parseDisjunction(disjunction: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            disjunction,
            AlRule.CONJUNCTION,
            AlTerminal.DISJ,
            { parseConjunction(it, symbolContext) }
        ) { x, y, _ ->
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

    private fun parseConjunction(conjunction: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            conjunction,
            AlRule.EQUALITY,
            AlTerminal.CONJ,
            { parseEquality(it, symbolContext) }
        ) { x, y, _ ->
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

    private fun parseEquality(equality: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            equality,
            AlRule.COMPARISON,
            AlRule.EQUALITY_OPERATOR,
            { parseComparison(it, symbolContext) }
        ) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlTerminal.EXCL_EQ -> "!="
                AlTerminal.EQEQ -> "=="
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

    private fun parseComparison(comparison: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            comparison,
            AlRule.INFIX_OPERATION,
            AlRule.COMPARISON_OPERATOR,
            { parseInfixOperation(it, symbolContext) }
        ) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlTerminal.LANGLE -> "<"
                AlTerminal.RANGLE -> ">"
                AlTerminal.LE -> "<="
                AlTerminal.GE -> ">="
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

    private fun parseInfixOperation(infixOperation: AlTree, symbolContext: SymbolContext): KtExpression {
        return if (infixOperation.contains(AlRule.IS_OPERATOR)) {
            if (infixOperation.children.size != 3) {
                throw LineException("unable to parse is expression", infixOperation.line)
            }
            val type = KtParserTypeIdentifier.parseType(infixOperation.find(AlRule.TYPE))
            val typeExpression = KtExpressionProperty(
                infixOperation.line,
                null,
                type,
                null,
                null
            )
            val identifier = when (infixOperation.find(AlRule.IS_OPERATOR).unwrap().index) {
                AlTerminal.IS -> "is"
                AlTerminal.NOT_IS -> "!is"
                else -> throw LineException("is operator expected", infixOperation.line)
            }
            val infixFunctionCall = infixOperation
                .find(AlRule.ELVIS_EXPRESSION)
                .find(AlRule.INFIX_FUNCTION_CALL)
            KtExpressionFunction(
                infixOperation.line,
                null,
                identifier,
                parseInfixFunctionCall(infixFunctionCall, symbolContext),
                listOf(typeExpression),
                null
            )
        } else {
            reduceBinaryOperator(
                infixOperation,
                AlRule.ELVIS_EXPRESSION,
                AlRule.IN_OPERATOR,
                { parseInfixFunctionCall(it.find(AlRule.INFIX_FUNCTION_CALL), symbolContext) }
            ) { x, y, op ->
                val identifier = when (op.unwrap().index) {
                    AlTerminal.IN -> "in"
                    AlTerminal.NOT_IN -> "!in"
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

    private fun parseInfixFunctionCall(infixFunctionCall: AlTree, symbolContext: SymbolContext): KtExpression {
        if (infixFunctionCall.children.isEmpty()) {
            throw LineException("rule node has no children", infixFunctionCall.line)
        }
        val iterator = infixFunctionCall.children.iterator()
        var expression = parseRangeExpression(iterator.next(), symbolContext)
        while (iterator.hasNext()) {
            val identifier = iterator.next().unwrap().text

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

    private fun parseInfixFunctionCallBlock(rangeExpression: AlTree, symbolContext: SymbolContext): KtBlock? {
        val primaryExpression = rangeExpression
            .let { if (it.children.size == 1) it.find(AlRule.ADDITIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.MULTIPLICATIVE_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.AS_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.COMPARISON_WITH_LITERAL_RIGHT_SIDE) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.PREFIX_UNARY_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.POSTFIX_UNARY_EXPRESSION) else null }
            ?.let { if (it.children.size == 1) it.find(AlRule.PRIMARY_EXPRESSION) else null }
        return if (primaryExpression != null && primaryExpression.contains(AlRule.FUNCTION_LITERAL)) {
            return primaryExpression
                .find(AlRule.FUNCTION_LITERAL)
                .find(AlRule.LAMBDA_LITERAL)
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

    private fun parseRangeExpression(rangeExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            rangeExpression,
            AlRule.ADDITIVE_EXPRESSION,
            AlTerminal.RANGE,
            { parseAdditiveExpression(it, symbolContext) }
        ) { x, y, _ ->
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

    private fun parseAdditiveExpression(additiveExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            additiveExpression,
            AlRule.MULTIPLICATIVE_EXPRESSION,
            AlRule.ADDITIVE_OPERATOR,
            { parseMultiplicativeExpression(it, symbolContext) }
        ) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlTerminal.ADD -> "+"
                AlTerminal.SUB -> "-"
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

    private fun parseMultiplicativeExpression(multiplicativeExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            multiplicativeExpression,
            AlRule.AS_EXPRESSION,
            AlRule.MULTIPLICATIVE_OPERATOR,
            { parseAsExpression(it, symbolContext) }
        ) { x, y, op ->
            val identifier = when (op.unwrap().index) {
                AlTerminal.MULT -> "*"
                AlTerminal.MOD -> "%"
                AlTerminal.DIV -> "/"
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

    private fun parseAsExpression(asExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val prefixUnaryExpression = asExpression
            .find(AlRule.COMPARISON_WITH_LITERAL_RIGHT_SIDE)
            .find(AlRule.PREFIX_UNARY_EXPRESSION)
        return if (asExpression.contains(AlRule.AS_OPERATOR)) {
            val type = KtParserTypeIdentifier.parseType(asExpression.find(AlRule.TYPE))
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
