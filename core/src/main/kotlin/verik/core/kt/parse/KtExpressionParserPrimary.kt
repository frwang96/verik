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

package verik.core.kt.parse

import verik.core.al.AlRule
import verik.core.al.AlRuleType
import verik.core.al.AlToken
import verik.core.al.AlTokenType
import verik.core.base.LineException
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_BREAK
import verik.core.lang.LangSymbol.OPERATOR_CONTINUE
import verik.core.lang.LangSymbol.OPERATOR_IF
import verik.core.lang.LangSymbol.OPERATOR_IF_ELSE
import verik.core.lang.LangSymbol.OPERATOR_RETURN
import verik.core.lang.LangSymbol.OPERATOR_RETURN_UNIT


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
                KtExpressionParserLiteral.parse(child)
            }
            AlRuleType.STRING_LITERAL -> {
                KtExpressionParserString.parse(child)
            }
            AlRuleType.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", primaryExpression)
            }
            AlRuleType.THIS_EXPRESSION -> {
                KtExpressionProperty(
                        primaryExpression.line,
                        null,
                        "this",
                        null,
                        null
                )
            }
            AlRuleType.SUPER_EXPRESSION -> {
                KtExpressionProperty(
                        primaryExpression.line,
                        null,
                        "super",
                        null,
                        null
                )
            }
            AlRuleType.IF_EXPRESSION -> {
                parseIfExpression(child)
            }
            AlRuleType.WHEN_EXPRESSION -> {
                parseWhenExpression(child)
            }
            AlRuleType.JUMP_EXPRESSION -> {
                parseJumpExpression(child)
            }
            else -> throw LineException("primary expression expected", primaryExpression)
        }
    }

    private fun parseIfExpression(ifExpression: AlRule): KtExpression {
        val condition = KtExpression(ifExpression.childAs(AlRuleType.EXPRESSION))
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
                    OPERATOR_IF_ELSE,
                    null,
                    listOf(condition),
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
                    OPERATOR_IF,
                    null,
                    listOf(condition),
                    listOf(ifBody)
            )
        }
    }

    private fun parseWhenExpression(whenExpression: AlRule): KtExpression {
        val condition = if (whenExpression.containsType(AlRuleType.WHEN_SUBJECT)) {
            KtExpression(whenExpression.childAs(AlRuleType.WHEN_SUBJECT).childAs(AlRuleType.EXPRESSION))
        } else null

        val whenEntries = whenExpression
                .childrenAs(AlRuleType.WHEN_ENTRY)
                .map { parseWhenEntry(it, condition) }

        var (count, expression) = when (whenEntries.count { it.first == null }) {
            0 -> {
                if (whenEntries.isEmpty()) {
                    throw LineException("unable to parse when expression", whenExpression)
                }
                Pair(whenEntries.size - 2, KtExpressionOperator(
                        whenEntries.last().first!!.line,
                        null,
                        OPERATOR_IF,
                        null,
                        listOf(whenEntries.last().first!!),
                        listOf(whenEntries.last().second)
                ))
            }
            1 -> {
                if (whenEntries.last().first != null || whenEntries.size == 1) {
                    throw LineException("unable to parse when expression", whenExpression)
                }
                Pair(whenEntries.size - 3, KtExpressionOperator(
                        whenEntries[whenEntries.size - 2].first!!.line,
                        null,
                        OPERATOR_IF_ELSE,
                        null,
                        listOf(whenEntries[whenEntries.size - 2].first!!),
                        listOf(
                                whenEntries[whenEntries.size - 2].second,
                                whenEntries.last().second
                        )
                ))
            }
            else -> {
                throw LineException("unable to parse when expression", whenExpression)
            }
        }

        while (count >= 0) {
            expression = KtExpressionOperator(
                    whenEntries[count].first!!.line,
                    null,
                    OPERATOR_IF_ELSE,
                    null,
                    listOf(whenEntries[count].first!!),
                    listOf(
                            whenEntries[count].second,
                            KtBlock(expression.line, listOf(KtStatementExpression(expression.line, expression)))
                    )
            )
            count -= 1
        }

        return expression
    }

    private fun parseWhenEntry(whenEntry: AlRule, condition: KtExpression?): Pair<KtExpression?, KtBlock> {
        val block = KtBlock(whenEntry.childAs(AlRuleType.CONTROL_STRUCTURE_BODY).firstAsRule())

        return if (whenEntry.containsType(AlTokenType.ELSE)) {
            Pair(null, block)
        } else {
            val whenConditions = whenEntry
                    .childrenAs(AlRuleType.WHEN_CONDITION)
                    .map { it.firstAsRule() }
            if (whenConditions.size != 1 || whenConditions[0].type != AlRuleType.EXPRESSION) {
                throw LineException("unable to parse when condition", whenEntry)
            }

            val expression = if (condition != null) {
                KtExpressionFunction(
                        whenEntry.line,
                        null,
                        "eq",
                        condition,
                        listOf(KtExpression(whenConditions[0])),
                        null
                )
            } else {
                KtExpression(whenConditions[0])
            }

            return Pair(expression, block)
        }
    }

    private fun parseJumpExpression(jumpExpression: AlRule): KtExpression {
        return when (jumpExpression.firstAsTokenType()) {
            AlTokenType.RETURN -> {
                if (jumpExpression.containsType(AlRuleType.EXPRESSION)) {
                    KtExpressionOperator(
                            jumpExpression.line,
                            null,
                            OPERATOR_RETURN,
                            null,
                            listOf(KtExpression(jumpExpression.childAs(AlRuleType.EXPRESSION))),
                            listOf()
                    )
                } else {
                    KtExpressionOperator(
                            jumpExpression.line,
                            null,
                            OPERATOR_RETURN_UNIT,
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
                        OPERATOR_CONTINUE,
                        null,
                        listOf(),
                        listOf()
                )
            }
            AlTokenType.BREAK -> {
                KtExpressionOperator(
                        jumpExpression.line,
                        null,
                        OPERATOR_BREAK,
                        null,
                        listOf(),
                        listOf()
                )
            }
            else -> throw LineException("return or continue or break expected", jumpExpression)
        }
    }
}