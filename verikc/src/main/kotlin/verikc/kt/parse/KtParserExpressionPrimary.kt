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
import verikc.al.AlTerminal
import verikc.al.AlTree
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_BREAK
import verikc.lang.LangSymbol.OPERATOR_CONTINUE
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_RETURN
import verikc.lang.LangSymbol.OPERATOR_RETURN_UNIT


object KtParserExpressionPrimary {

    fun parse(primaryExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val child = primaryExpression.unwrap()

        return when (child.index) {
            AlRule.PARENTHESIZED_EXPRESSION -> {
                KtParserExpression.parse(child.find(AlRule.EXPRESSION), symbolContext)
            }
            AlRule.SIMPLE_IDENTIFIER -> {
                val identifier = child.find(AlTerminal.IDENTIFIER).text!!
                KtExpressionProperty(primaryExpression.line, null, identifier, null, null)
            }
            AlRule.LITERAL_CONSTANT -> {
                KtParserLiteral.parse(child)
            }
            AlRule.STRING_LITERAL -> {
                KtParserExpressionString.parse(child, symbolContext)
            }
            AlRule.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", primaryExpression.line)
            }
            AlRule.THIS_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "this", null, null)
            }
            AlRule.SUPER_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "super", null, null)
            }
            AlRule.IF_EXPRESSION -> {
                parseIfExpression(child, symbolContext)
            }
            AlRule.WHEN_EXPRESSION -> {
                parseWhenExpression(child, symbolContext)
            }
            AlRule.JUMP_EXPRESSION -> {
                parseJumpExpression(child, symbolContext)
            }
            else -> throw LineException("primary expression expected", primaryExpression.line)
        }
    }

    private fun parseIfExpression(ifExpression: AlTree, SymbolContext: SymbolContext): KtExpression {
        val condition = KtExpression(ifExpression.find(AlRule.EXPRESSION), SymbolContext)
        return if (ifExpression.contains(AlTerminal.ELSE)) {
            var ifBody: KtBlock? = null
            var elseBody: KtBlock? = null

            var isIf = true
            for (child in ifExpression.children) {
                if (child.index == AlTerminal.ELSE) {
                    isIf = false
                } else if (child.index == AlRule.CONTROL_STRUCTURE_BODY) {
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
            val ifBody = if (ifExpression.contains(AlRule.CONTROL_STRUCTURE_BODY)) {
                KtParserBlock.parseControlStructureBody(
                    ifExpression.find(AlRule.CONTROL_STRUCTURE_BODY),
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

    private fun parseWhenExpression(whenExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val condition = if (whenExpression.contains(AlRule.WHEN_SUBJECT)) {
            KtExpression(
                whenExpression.find(AlRule.WHEN_SUBJECT).find(AlRule.EXPRESSION),
                symbolContext
            )
        } else null

        val whenEntries = whenExpression
            .findAll(AlRule.WHEN_ENTRY)
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
        whenEntry: AlTree,
        condition: KtExpression?,
        symbolContext: SymbolContext
    ): Pair<KtExpression?, KtBlock> {
        val block = KtParserBlock.parseControlStructureBody(
            whenEntry.find(AlRule.CONTROL_STRUCTURE_BODY),
            symbolContext
        )

        return if (whenEntry.contains(AlTerminal.ELSE)) {
            Pair(null, block)
        } else {
            val whenConditions = whenEntry
                .findAll(AlRule.WHEN_CONDITION)
                .map { it.unwrap() }
            if (whenConditions.size != 1 || whenConditions[0].index != AlRule.EXPRESSION) {
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

    private fun parseJumpExpression(jumpExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        if (jumpExpression.contains(AlTerminal.RETURN)) {
            return if (jumpExpression.contains(AlRule.EXPRESSION)) {
                KtExpressionOperator(
                    jumpExpression.line,
                    null,
                    OPERATOR_RETURN,
                    null,
                    listOf(KtExpression(jumpExpression.find(AlRule.EXPRESSION), symbolContext)),
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

        return when (jumpExpression.unwrap().index) {
            AlTerminal.CONTINUE -> {
                KtExpressionOperator(
                    jumpExpression.line,
                    null,
                    OPERATOR_CONTINUE,
                    null,
                    listOf(),
                    listOf()
                )
            }
            AlTerminal.BREAK -> {
                KtExpressionOperator(
                    jumpExpression.line,
                    null,
                    OPERATOR_BREAK,
                    null,
                    listOf(),
                    listOf()
                )
            }
            else -> throw LineException("continue or break expected", jumpExpression.line)
        }
    }
}
