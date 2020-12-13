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
import verikc.al.AlToken
import verikc.al.AlTokenType
import verikc.base.SymbolContext
import verikc.base.ast.LineException
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_BREAK
import verikc.lang.LangSymbol.OPERATOR_CONTINUE
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_RETURN
import verikc.lang.LangSymbol.OPERATOR_RETURN_UNIT


object KtParserExpressionPrimary {

    fun parse(primaryExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        val child = primaryExpression.firstAsRule()

        return when (child.type) {
            AlRuleType.PARENTHESIZED_EXPRESSION -> {
                KtParserExpression.parse(child.firstAsRule(), symbolContext)
            }
            AlRuleType.SIMPLE_IDENTIFIER -> {
                KtExpressionProperty(primaryExpression.line, null, child.firstAsTokenText(), null, null)
            }
            AlRuleType.LITERAL_CONSTANT -> {
                KtParserLiteral.parse(child)
            }
            AlRuleType.STRING_LITERAL -> {
                KtParserExpressionString.parse(child, symbolContext)
            }
            AlRuleType.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", primaryExpression.line)
            }
            AlRuleType.THIS_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "this", null, null)
            }
            AlRuleType.SUPER_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "super", null, null)
            }
            AlRuleType.IF_EXPRESSION -> {
                parseIfExpression(child, symbolContext)
            }
            AlRuleType.WHEN_EXPRESSION -> {
                parseWhenExpression(child, symbolContext)
            }
            AlRuleType.JUMP_EXPRESSION -> {
                parseJumpExpression(child, symbolContext)
            }
            else -> throw LineException("primary expression expected", primaryExpression.line)
        }
    }

    private fun parseIfExpression(ifExpression: AlRule, SymbolContext: SymbolContext): KtExpression {
        val condition = KtExpression(ifExpression.childAs(AlRuleType.EXPRESSION), SymbolContext)
        return if (ifExpression.containsType(AlTokenType.ELSE)) {
            var ifBody: KtBlock? = null
            var elseBody: KtBlock? = null

            var isIf = true
            for (child in ifExpression.children) {
                if (child is AlToken && child.type == AlTokenType.ELSE) {
                    isIf = false
                } else if (child is AlRule && child.type == AlRuleType.CONTROL_STRUCTURE_BODY) {
                    if (isIf) {
                        ifBody = KtParserBlock.parseControlStructureBody(child, SymbolContext)
                    } else {
                        elseBody = KtParserBlock.parseControlStructureBody(child, SymbolContext)
                    }
                }
            }

            ifBody = ifBody ?: KtParserBlock.emptyBlock(ifExpression.line, SymbolContext)
            elseBody = elseBody ?: KtParserBlock.emptyBlock(ifExpression.line, SymbolContext)

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
                KtParserBlock.parseControlStructureBody(
                    ifExpression.childAs(AlRuleType.CONTROL_STRUCTURE_BODY),
                    SymbolContext
                )
            } else {
                KtParserBlock.emptyBlock(ifExpression.line, SymbolContext)
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

    private fun parseWhenExpression(whenExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        val condition = if (whenExpression.containsType(AlRuleType.WHEN_SUBJECT)) {
            KtExpression(
                whenExpression.childAs(AlRuleType.WHEN_SUBJECT).childAs(AlRuleType.EXPRESSION),
                symbolContext
            )
        } else null

        val whenEntries = whenExpression
            .childrenAs(AlRuleType.WHEN_ENTRY)
            .map { parseWhenEntry(it, condition, symbolContext) }

        var (count, expression) = when (whenEntries.count { it.first == null }) {
            0 -> {
                if (whenEntries.isEmpty()) {
                    throw LineException("unable to parse when expression", whenExpression.line)
                }
                Pair(
                    whenEntries.size - 2,
                    KtExpressionOperator(
                        whenEntries.last().first!!.line,
                        null,
                        OPERATOR_IF,
                        null,
                        listOf(whenEntries.last().first!!),
                        listOf(whenEntries.last().second)
                    )
                )
            }
            1 -> {
                if (whenEntries.last().first != null || whenEntries.size == 1) {
                    throw LineException("unable to parse when expression", whenExpression.line)
                }
                Pair(
                    whenEntries.size - 3,
                    KtExpressionOperator(
                        whenEntries[whenEntries.size - 2].first!!.line,
                        null,
                        OPERATOR_IF_ELSE,
                        null,
                        listOf(whenEntries[whenEntries.size - 2].first!!),
                        listOf(
                            whenEntries[whenEntries.size - 2].second,
                            whenEntries.last().second
                        )
                    )
                )
            }
            else -> {
                throw LineException("unable to parse when expression", whenExpression.line)
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
                    KtParserBlock.expressionBlock(expression, symbolContext)
                )
            )
            count -= 1
        }

        return expression
    }

    private fun parseWhenEntry(
        whenEntry: AlRule,
        condition: KtExpression?,
        symbolContext: SymbolContext
    ): Pair<KtExpression?, KtBlock> {
        val block = KtParserBlock.parseControlStructureBody(
            whenEntry.childAs(AlRuleType.CONTROL_STRUCTURE_BODY),
            symbolContext
        )

        return if (whenEntry.containsType(AlTokenType.ELSE)) {
            Pair(null, block)
        } else {
            val whenConditions = whenEntry
                .childrenAs(AlRuleType.WHEN_CONDITION)
                .map { it.firstAsRule() }
            if (whenConditions.size != 1 || whenConditions[0].type != AlRuleType.EXPRESSION) {
                throw LineException("unable to parse when condition", whenEntry.line)
            }

            val expression = if (condition != null) {
                KtExpressionFunction(
                    whenEntry.line,
                    null,
                    "==",
                    condition,
                    listOf(KtExpression(whenConditions[0], symbolContext)),
                    null
                )
            } else {
                KtExpression(whenConditions[0], symbolContext)
            }

            return Pair(expression, block)
        }
    }

    private fun parseJumpExpression(jumpExpression: AlRule, symbolContext: SymbolContext): KtExpression {
        return when (jumpExpression.firstAsTokenType()) {
            AlTokenType.RETURN -> {
                if (jumpExpression.containsType(AlRuleType.EXPRESSION)) {
                    KtExpressionOperator(
                        jumpExpression.line,
                        null,
                        OPERATOR_RETURN,
                        null,
                        listOf(KtExpression(jumpExpression.childAs(AlRuleType.EXPRESSION), symbolContext)),
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
            else -> throw LineException("return or continue or break expected", jumpExpression.line)
        }
    }
}
