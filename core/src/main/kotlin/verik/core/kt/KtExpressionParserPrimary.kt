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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.al.AlToken
import verik.core.al.AlTokenType
import verik.core.main.LineException


object KtExpressionParserPrimary {

    fun parse(primaryExpression: AlRule): KtExpression {
        val child = primaryExpression.firstAsRule()

        return when(child.type) {
            AlRuleType.PARENTHESIZED_EXPRESSION -> {
                KtExpressionParser.parse(child.firstAsRule())
            }
            AlRuleType.SIMPLE_IDENTIFIER -> {
                KtExpressionProperty(
                        primaryExpression.line,
                        null,
                        child.firstAsTokenText(),
                        null,
                        null
                )
            }
            AlRuleType.LITERAL_CONSTANT -> {
                KtExpressionLiteral(
                        primaryExpression.line,
                        null,
                        child.firstAsTokenText()
                )
            }
            AlRuleType.STRING_LITERAL -> {
                KtExpressionParserString.parse(child)
            }
            AlRuleType.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", primaryExpression)
            }
            AlRuleType.THIS_EXPRESSION -> {
                KtExpressionLiteral(
                        primaryExpression.line,
                        null,
                        "this"
                )
            }
            AlRuleType.SUPER_EXPRESSION -> {
                KtExpressionLiteral(
                        primaryExpression.line,
                        null,
                        "super"
                )
            }
            AlRuleType.IF_EXPRESSION -> {
                parseIfExpression(child)
            }
            AlRuleType.WHEN_EXPRESSION -> {
                throw LineException("when expressions are not supported", primaryExpression)
            }
            AlRuleType.JUMP_EXPRESSION -> {
                parseJumpExpression(child)
            }
            else -> throw LineException("primary expression expected", primaryExpression)
        }
    }

    private fun parseIfExpression(ifExpression: AlRule): KtExpression {
        val target = KtExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
        return if (ifExpression.containsType(AlTokenType.ELSE)) {
            var ifBody = KtBlock(ifExpression.line, listOf())
            var elseBody = KtBlock(ifExpression.line, listOf())
            var isIf = true
            for (child in ifExpression.children) {
                if (child is AlToken && child.type == AlTokenType.ELSE) {
                    isIf = false
                } else if (child is AlRule && child.type == AlRuleType.CONTROL_STRUCTURE_BODY) {
                    if (isIf) {
                        ifBody = KtBlock(child.firstAsRule())
                    } else {
                        elseBody = KtBlock(child.firstAsRule())
                    }
                }
            }
            KtExpressionOperator(
                    ifExpression.line,
                    null,
                    KtOperatorIdentifier.IF_ELSE,
                    target,
                    listOf(),
                    listOf(ifBody, elseBody)
            )
        } else {
            val ifBody = if (ifExpression.containsType(AlRuleType.CONTROL_STRUCTURE_BODY)) {
                val child = ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY)
                KtBlock(child.firstAsRule())
            } else {
                KtBlock(ifExpression.line, listOf())
            }
            KtExpressionOperator(
                    ifExpression.line,
                    null,
                    KtOperatorIdentifier.IF,
                    target,
                    listOf(),
                    listOf(ifBody)
            )
        }
    }

    private fun parseJumpExpression(jumpExpression: AlRule): KtExpression {
        return when (jumpExpression.firstAsTokenType()) {
            AlTokenType.RETURN -> {
                if (jumpExpression.containsType(AlRuleType.EXPRESSION)) {
                    KtExpressionOperator(
                            jumpExpression.line,
                            null,
                            KtOperatorIdentifier.RETURN,
                            null,
                            listOf(KtExpression(jumpExpression.childAs(AlRuleType.EXPRESSION))),
                            listOf()
                    )
                } else {
                    KtExpressionOperator(
                            jumpExpression.line,
                            null,
                            KtOperatorIdentifier.RETURN_UNIT,
                            null,
                            listOf(),
                            listOf()
                    )
                }
            }
            AlTokenType.CONTINUE -> {
                KtExpressionOperator(
                        jumpExpression.line,
                        null,
                        KtOperatorIdentifier.CONTINUE,
                        null,
                        listOf(),
                        listOf()
                )
            }
            AlTokenType.BREAK -> {
                KtExpressionOperator(
                        jumpExpression.line,
                        null,
                        KtOperatorIdentifier.BREAK,
                        null,
                        listOf(),
                        listOf()
                )
            }
            else -> throw LineException("return or continue or break expected", jumpExpression)
        }
    }
}