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

package io.verik.core.kt

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlToken
import io.verik.core.al.AlTokenType

class KtExpressionParser {

    companion object {

        fun parse(expression: AlRule): KtExpression {
            return parseDisjunction(expression.childAs(AlRuleType.DISJUNCTION))
        }

        private fun reduce(
                root: AlRule,
                map: (AlRule) -> KtExpression,
                acc: (KtExpression, KtExpression) -> KtExpression
        ): KtExpression {
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
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
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
            val iterator = root.children.iterator()
            var x = map(iterator.next().asRule())
            while (iterator.hasNext()) {
                val op = iterator.next().asRule()
                if (!iterator.hasNext()) throw LineException("expression expected", root)
                val y = map(iterator.next().asRule())
                x = acc(x, y, op)
            }
            return x
        }

        private fun reduceLeft(
                root: AlRule,
                map: (AlRule) -> KtExpression,
                acc: (KtExpression, AlRule) -> KtExpression
        ): KtExpression {
            if (root.children.isEmpty()) throw LineException("rule node has no children", root)
            val reversedChildren = root.children.reversed()
            var x = map(reversedChildren[0].asRule())
            for (child in reversedChildren.drop(1)) {
                x = acc(x, child.asRule())
            }
            return x
        }

        private fun parseDisjunction(disjunction: AlRule): KtExpression {
            return reduce(disjunction, { parseConjunction(it) }) { x, y ->
                KtExpressionFunction(disjunction.line, x, KtFunctionIdentifierOperator(KtOperatorType.OR), listOf(y))
            }
        }

        private fun parseConjunction(conjunction: AlRule): KtExpression {
            return reduce(conjunction, { parseEquality(it) }) { x, y ->
                KtExpressionFunction(conjunction.line, x, KtFunctionIdentifierOperator(KtOperatorType.AND), listOf(y))
            }
        }

        private fun parseEquality(equality: AlRule): KtExpression {
            return reduceOp(equality, { parseComparison(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.EQEQ -> KtOperatorType.EQ
                    AlTokenType.EXCL_EQ -> KtOperatorType.NOT_EQ
                    else -> throw LineException("equality operator expected", equality)
                }
                KtExpressionFunction(equality.line, x, KtFunctionIdentifierOperator(type), listOf(y))
            }
        }

        private fun parseComparison(comparison: AlRule): KtExpression {
            return reduceOp(comparison, { parseInfixOperation(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.LANGLE -> KtOperatorType.LT
                    AlTokenType.RANGLE -> KtOperatorType.GT
                    AlTokenType.LE -> KtOperatorType.LT_EQ
                    AlTokenType.GE -> KtOperatorType.GT_EQ
                    else -> throw LineException("comparison operator expected", comparison)
                }
                KtExpressionFunction(comparison.line, x, KtFunctionIdentifierOperator(type), listOf(y))
            }
        }

        private fun parseInfixOperation(infixOperation: AlRule): KtExpression {
            return reduceOp(infixOperation, { parseInfixFunctionCall(it.firstAsRule()) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.IN -> KtOperatorType.IN
                    AlTokenType.NOT_IN -> KtOperatorType.NOT_IN
                    else -> throw LineException("infix operator expected", infixOperation)
                }
                KtExpressionFunction(infixOperation.line, x, KtFunctionIdentifierOperator(type), listOf(y))
            }
        }

        private fun parseInfixFunctionCall(infixFunctionCall: AlRule): KtExpression {
            return reduceOp(infixFunctionCall, { parseRangeExpression(it) }) { x, y, op ->
                val name = op.firstAsTokenText()
                KtExpressionFunction(infixFunctionCall.line, x, KtFunctionIdentifierNamed(name, true), listOf(y))
            }
        }

        private fun parseRangeExpression(rangeExpression: AlRule): KtExpression {
            return reduce(rangeExpression, { parseAdditiveExpression(it) }) { x, y ->
                KtExpressionFunction(rangeExpression.line, x, KtFunctionIdentifierOperator(KtOperatorType.RANGE), listOf(y))
            }
        }

        private fun parseAdditiveExpression(additiveExpression: AlRule): KtExpression {
            return reduceOp(additiveExpression, { parseMultiplicativeExpression(it) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.ADD -> KtOperatorType.ADD
                    AlTokenType.SUB -> KtOperatorType.SUB
                    else -> throw LineException("additive operator expected", additiveExpression)
                }
                KtExpressionFunction(additiveExpression.line, x, KtFunctionIdentifierOperator(type), listOf(y))
            }
        }

        private fun parseMultiplicativeExpression(multiplicativeExpression: AlRule): KtExpression {
            return reduceOp(multiplicativeExpression, { parsePrefixUnaryExpression(it.firstAsRule().firstAsRule()) }) { x, y, op ->
                val type = when (op.firstAsTokenType()) {
                    AlTokenType.MULT -> KtOperatorType.MUL
                    AlTokenType.MOD -> KtOperatorType.MOD
                    AlTokenType.DIV -> KtOperatorType.DIV
                    else -> throw LineException("multiplicative operator expected", multiplicativeExpression)
                }
                KtExpressionFunction(multiplicativeExpression.line, x, KtFunctionIdentifierOperator(type), listOf(y))
            }
        }

        private fun parsePrefixUnaryExpression(prefixUnaryExpression: AlRule): KtExpression {
            return reduceLeft(prefixUnaryExpression, { parsePostfixUnaryExpression(it) }) { x, op ->
                val operator = op.firstAsRule().first()
                val type = when {
                    operator is AlToken && operator.type == AlTokenType.ADD -> KtOperatorType.UNARY_ADD
                    operator is AlToken && operator.type == AlTokenType.SUB -> KtOperatorType.UNARY_SUB
                    operator is AlRule && operator.type == AlRuleType.EXCL -> KtOperatorType.NOT
                    else -> throw LineException("prefix unary operator expected", prefixUnaryExpression)
                }
                KtExpressionFunction(prefixUnaryExpression.line, x, KtFunctionIdentifierOperator(type), listOf())
            }
        }

        private fun parsePostfixUnaryExpression(postfixUnaryExpression: AlRule): KtExpression {
            val primaryExpression = KtPrimaryExpressionParser.parse(postfixUnaryExpression.childAs(AlRuleType.PRIMARY_EXPRESSION))
            var expression: KtExpression? = null
            var identifier: String? = null
            if (primaryExpression is KtExpressionProperty && primaryExpression.target == null) {
                identifier = primaryExpression.identifier
            } else {
                expression = primaryExpression
            }
            val suffixes = postfixUnaryExpression.childrenAs(AlRuleType.POSTFIX_UNARY_SUFFIX).map { it.firstAsRule() }
            for (suffix in suffixes) {
                if (suffix.type == AlRuleType.CALL_SUFFIX) {
                    if (identifier != null) {
                        val args = suffix
                                .childrenAs(AlRuleType.VALUE_ARGUMENTS)
                                .flatMap { it.childrenAs(AlRuleType.VALUE_ARGUMENT) }
                                .map { it.childAs(AlRuleType.EXPRESSION) }
                                .map { KtExpression(it) }
                        val lambdaArgs = suffix
                                .childrenAs(AlRuleType.ANNOTATED_LAMBDA)
                                .map { it.childAs(AlRuleType.LAMBDA_LITERAL) }
                                .map { KtPrimaryExpressionParser.parseLambdaLiteral(it) }
                        expression = KtExpressionFunction(
                                postfixUnaryExpression.line,
                                expression,
                                KtFunctionIdentifierNamed(identifier, false),
                                args + lambdaArgs
                        )
                        identifier = null
                    } else {
                        throw LineException("illegal call target", postfixUnaryExpression)
                    }
                } else {
                    if (identifier != null) {
                        expression = KtExpressionProperty(
                                postfixUnaryExpression.line,
                                expression,
                                identifier
                        )
                        identifier = null
                    }
                    when (suffix.type) {
                        AlRuleType.INDEXING_SUFFIX -> {
                            val args = suffix.childrenAs(AlRuleType.EXPRESSION).map { KtExpression(it) }
                            expression = KtExpressionFunction(
                                    postfixUnaryExpression.line,
                                    expression,
                                    KtFunctionIdentifierOperator(KtOperatorType.GET),
                                    args
                            )
                        }
                        AlRuleType.NAVIGATION_SUFFIX -> {
                            identifier = suffix.childAs(AlRuleType.SIMPLE_IDENTIFIER).firstAsTokenText()
                        }
                        else -> throw LineException("postfix unary suffix expected", postfixUnaryExpression)
                    }
                }
            }
            if (identifier != null) {
                expression = KtExpressionProperty(
                        postfixUnaryExpression.line,
                        expression,
                        identifier
                )
            }
            return expression!!
        }
    }
}