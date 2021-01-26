/*
 * Copyright (c) 2020 Francis Wang
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
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtExpression
import verikc.kt.ast.KtExpressionFunction
import verikc.kt.ast.KtExpressionProperty

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
            KtExpressionFunction(disjunction.line, "||", x, null, listOf(y))
        }
    }

    private fun parseConjunction(conjunction: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            conjunction,
            AlRule.EQUALITY,
            AlTerminal.CONJ,
            { parseEquality(it, symbolContext) }
        ) { x, y, _ ->
            KtExpressionFunction(conjunction.line, "&&", x, null, listOf(y))
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
            KtExpressionFunction(equality.line, identifier, x, null, listOf(y))
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
            KtExpressionFunction(comparison.line, identifier, x, null, listOf(y))
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
                type,
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
                identifier,
                parseInfixFunctionCall(infixFunctionCall, symbolContext),
                null,
                listOf(typeExpression)
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
                KtExpressionFunction(infixOperation.line, identifier, x, null, listOf(y))
            }
        }
    }

    private fun parseInfixFunctionCall(infixFunctionCall: AlTree, symbolContext: SymbolContext): KtExpression {
        return reduceBinaryOperator(
            infixFunctionCall,
            AlRule.RANGE_EXPRESSION,
            AlRule.SIMPLE_IDENTIFIER,
            { parseAdditiveExpression(it.find(AlRule.ADDITIVE_EXPRESSION), symbolContext) }
        ) { x, y, op ->
            KtExpressionFunction(infixFunctionCall.line, op.unwrap().text, x, null, listOf(y))
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
            KtExpressionFunction(additiveExpression.line, identifier, x, null, listOf(y))
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
            KtExpressionFunction(multiplicativeExpression.line, identifier, x, null, listOf(y))
        }
    }

    private fun parseAsExpression(asExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val prefixUnaryExpression = asExpression
            .find(AlRule.COMPARISON_WITH_LITERAL_RIGHT_SIDE)
            .find(AlRule.PREFIX_UNARY_EXPRESSION)
        return if (asExpression.contains(AlRule.AS_OPERATOR)) {
            val type = KtParserTypeIdentifier.parseType(asExpression.find(AlRule.TYPE))
            val typeExpression = KtExpressionProperty(asExpression.line, type, null)
            KtExpressionFunction(
                asExpression.line,
                "as",
                KtParserExpressionUnary.parsePrefixUnaryExpression(prefixUnaryExpression, symbolContext),
                null,
                listOf(typeExpression)
            )
        } else {
            KtParserExpressionUnary.parsePrefixUnaryExpression(prefixUnaryExpression, symbolContext)
        }
    }
}
