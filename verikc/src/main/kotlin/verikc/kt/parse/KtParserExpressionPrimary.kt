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

    fun parse(primaryExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        val child = primaryExpression.unwrap()

        return when (child.index) {
            AlxRuleIndex.PARENTHESIZED_EXPRESSION -> {
                KtParserExpression.parse(child.find(AlxRuleIndex.EXPRESSION), symbolContext)
            }
            AlxRuleIndex.SIMPLE_IDENTIFIER -> {
                val identifier = child.find(AlxTerminalIndex.IDENTIFIER).text!!
                KtExpressionProperty(primaryExpression.line, null, identifier, null, null)
            }
            AlxRuleIndex.LITERAL_CONSTANT -> {
                KtParserLiteral.parse(child)
            }
            AlxRuleIndex.STRING_LITERAL -> {
                KtParserExpressionString.parse(child, symbolContext)
            }
            AlxRuleIndex.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", primaryExpression.line)
            }
            AlxRuleIndex.THIS_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "this", null, null)
            }
            AlxRuleIndex.SUPER_EXPRESSION -> {
                KtExpressionProperty(primaryExpression.line, null, "super", null, null)
            }
            AlxRuleIndex.IF_EXPRESSION -> {
                parseIfExpression(child, symbolContext)
            }
            AlxRuleIndex.WHEN_EXPRESSION -> {
                parseWhenExpression(child, symbolContext)
            }
            AlxRuleIndex.JUMP_EXPRESSION -> {
                parseJumpExpression(child, symbolContext)
            }
            else -> throw LineException("primary expression expected", primaryExpression.line)
        }
    }

    private fun parseIfExpression(ifExpression: AlxTree, SymbolContext: SymbolContext): KtExpression {
        val condition = KtExpression(ifExpression.find(AlxRuleIndex.EXPRESSION), SymbolContext)
        return if (ifExpression.contains(AlxTerminalIndex.ELSE)) {
            var ifBody: KtBlock? = null
            var elseBody: KtBlock? = null

            var isIf = true
            for (child in ifExpression.children) {
                if (child.index == AlxTerminalIndex.ELSE) {
                    isIf = false
                } else if (child.index == AlxRuleIndex.CONTROL_STRUCTURE_BODY) {
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
            val ifBody = if (ifExpression.contains(AlxRuleIndex.CONTROL_STRUCTURE_BODY)) {
                KtParserBlock.parseControlStructureBody(
                    ifExpression.find(AlxRuleIndex.CONTROL_STRUCTURE_BODY),
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

    private fun parseWhenExpression(whenExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        val condition = if (whenExpression.contains(AlxRuleIndex.WHEN_SUBJECT)) {
            KtExpression(
                whenExpression.find(AlxRuleIndex.WHEN_SUBJECT).find(AlxRuleIndex.EXPRESSION),
                symbolContext
            )
        } else null

        val whenEntries = whenExpression
            .findAll(AlxRuleIndex.WHEN_ENTRY)
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
        whenEntry: AlxTree,
        condition: KtExpression?,
        symbolContext: SymbolContext
    ): Pair<KtExpression?, KtBlock> {
        val block = KtParserBlock.parseControlStructureBody(
            whenEntry.find(AlxRuleIndex.CONTROL_STRUCTURE_BODY),
            symbolContext
        )

        return if (whenEntry.contains(AlxTerminalIndex.ELSE)) {
            Pair(null, block)
        } else {
            val whenConditions = whenEntry
                .findAll(AlxRuleIndex.WHEN_CONDITION)
                .map { it.unwrap() }
            if (whenConditions.size != 1 || whenConditions[0].index != AlxRuleIndex.EXPRESSION) {
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

    private fun parseJumpExpression(jumpExpression: AlxTree, symbolContext: SymbolContext): KtExpression {
        if (jumpExpression.contains(AlxTerminalIndex.RETURN)) {
            return if (jumpExpression.contains(AlxRuleIndex.EXPRESSION)) {
                KtExpressionOperator(
                    jumpExpression.line,
                    null,
                    OPERATOR_RETURN,
                    null,
                    listOf(KtExpression(jumpExpression.find(AlxRuleIndex.EXPRESSION), symbolContext)),
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
            AlxTerminalIndex.CONTINUE -> {
                KtExpressionOperator(
                    jumpExpression.line,
                    null,
                    OPERATOR_CONTINUE,
                    null,
                    listOf(),
                    listOf()
                )
            }
            AlxTerminalIndex.BREAK -> {
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
