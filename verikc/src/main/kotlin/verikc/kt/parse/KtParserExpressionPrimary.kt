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
import verikc.base.ast.MutabilityType
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*
import verikc.lang.LangSymbol.OPERATOR_BREAK
import verikc.lang.LangSymbol.OPERATOR_CONTINUE
import verikc.lang.LangSymbol.OPERATOR_IF
import verikc.lang.LangSymbol.OPERATOR_IF_ELSE
import verikc.lang.LangSymbol.OPERATOR_RETURN
import verikc.lang.LangSymbol.OPERATOR_RETURN_UNIT
import verikc.lang.LangSymbol.OPERATOR_WHEN_BODY
import verikc.lang.LangSymbol.OPERATOR_WHEN_WRAPPER


object KtParserExpressionPrimary {

    fun parse(primaryExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val child = primaryExpression.unwrap()

        return when (child.index) {
            AlRule.PARENTHESIZED_EXPRESSION -> {
                KtParserExpressionBase.parse(child.find(AlRule.EXPRESSION), symbolContext)
            }
            AlRule.SIMPLE_IDENTIFIER -> {
                val identifier = child.unwrap().text
                KtExpressionProperty(child.line, identifier, null)
            }
            AlRule.LITERAL_CONSTANT -> {
                KtExpressionLiteral(child.line, child.unwrap().text)
            }
            AlRule.STRING_LITERAL -> {
                KtParserExpressionString.parse(child, symbolContext)
            }
            AlRule.FUNCTION_LITERAL -> {
                throw LineException("lambda literals are not permitted", child.line)
            }
            AlRule.THIS_EXPRESSION -> {
                KtExpressionProperty(child.line, "this", null)
            }
            AlRule.SUPER_EXPRESSION -> {
                KtExpressionProperty(child.line, "super", null)
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
            else -> throw LineException("primary expression expected", child.line)
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
            KtExpressionOperator(ifExpression.line, OPERATOR_IF_ELSE, condition, listOf(), listOf(ifBody, elseBody))
        } else {
            val ifBody = if (ifExpression.contains(AlRule.CONTROL_STRUCTURE_BODY)) {
                KtParserBlock.parseControlStructureBody(
                    ifExpression.find(AlRule.CONTROL_STRUCTURE_BODY),
                    SymbolContext
                )
            } else {
                KtParserBlock.emptyBlock(ifExpression.line, SymbolContext)
            }
            KtExpressionOperator(ifExpression.line, OPERATOR_IF, condition, listOf(), listOf(ifBody))
        }
    }

    private fun parseWhenExpression(whenExpression: AlTree, symbolContext: SymbolContext): KtExpression {
        val condition = if (whenExpression.contains(AlRule.WHEN_SUBJECT)) {
            KtExpression(
                whenExpression.find(AlRule.WHEN_SUBJECT).find(AlRule.EXPRESSION),
                symbolContext
            )
        } else null
        val conditionProperty = if (condition != null) {
            KtProperty(
                condition.line,
                "it",
                symbolContext.registerSymbol("it"),
                MutabilityType.VAL,
                listOf(),
                null,
                condition
            )
        } else null

        val whenEntries = whenExpression
            .findAll(AlRule.WHEN_ENTRY)
            .map { parseWhenEntry(it, conditionProperty, symbolContext) }

        val whenBody = KtExpressionOperator(
            whenExpression.line,
            OPERATOR_WHEN_BODY,
            null,
            whenEntries.mapNotNull { it.first },
            whenEntries.map { it.second }
        )
        when (whenBody.args.size) {
            whenBody.blocks.size -> {}
            whenBody.blocks.size - 1 -> {
                if (whenEntries.last().first != null)
                    throw LineException("else entry of when expression must come last", whenExpression.line)
            }
            else -> throw LineException("unable to parse when expression", whenExpression.line)
        }

        return if (conditionProperty != null) {
            KtExpressionOperator(
                whenExpression.line,
                OPERATOR_WHEN_WRAPPER,
                null,
                condition?.let { listOf(it) } ?: listOf(),
                listOf(
                    KtBlock(
                        whenExpression.line,
                        symbolContext.registerSymbol("block"),
                        listOf(conditionProperty),
                        listOf(KtStatementExpression(whenBody))
                    )
                )
            )
        } else {
            whenBody
        }
    }

    private fun parseWhenEntry(
        whenEntry: AlTree,
        conditionProperty: KtProperty?,
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

            val expression = if (conditionProperty != null) {
                KtExpressionFunction(
                    whenEntry.line,
                    "==",
                    KtExpressionProperty(whenEntry.line, conditionProperty.identifier, null),
                    null,
                    listOf(KtExpression(whenConditions[0], symbolContext))
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
                    OPERATOR_RETURN,
                    null,
                    listOf(KtExpression(jumpExpression.find(AlRule.EXPRESSION), symbolContext)),
                    listOf()
                )
            } else {
                KtExpressionOperator(
                    jumpExpression.line,
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
                    OPERATOR_CONTINUE,
                    null,
                    listOf(),
                    listOf()
                )
            }
            AlTerminal.BREAK -> {
                KtExpressionOperator(
                    jumpExpression.line,
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
