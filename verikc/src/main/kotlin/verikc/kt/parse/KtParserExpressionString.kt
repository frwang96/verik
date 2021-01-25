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
import verikc.kt.ast.KtExpressionLiteral
import verikc.kt.ast.KtExpressionProperty

object KtParserExpressionString {

    fun parse(stringLiteral: AlTree, symbolContext: SymbolContext): KtExpression {
        val expressions = joinExpressions(parseStringLiteral(stringLiteral, symbolContext))
        return when {
            expressions.isEmpty() -> KtExpressionLiteral(stringLiteral.line, "\"\"")
            expressions.size == 1 && isStringLiteral(expressions[0]) -> expressions[0]
            else -> {
                KtExpressionFunction(stringLiteral.line, "\"\"", null, null, expressions)
            }
        }
    }

    private fun parseStringLiteral(stringLiteral: AlTree, symbolContext: SymbolContext): List<KtExpression> {
        val lineStringLiteral = stringLiteral.find(AlRule.LINE_STRING_LITERAL)
        return lineStringLiteral.children.mapNotNull {
            when (it.index) {
                AlRule.LINE_STRING_CONTENT -> parseLineStringContent(it.unwrap())
                AlRule.LINE_STRING_EXPRESSION -> KtExpression(it.find(AlRule.EXPRESSION), symbolContext)
                AlTerminal.QUOTE_OPEN -> null
                AlTerminal.QUOTE_CLOSE -> null
                else -> throw LineException("line string content or expression expected", it.line)
            }
        }
    }

    private fun parseLineStringContent(lineStringContent: AlTree): KtExpression {
        val text = lineStringContent.text
        return when (lineStringContent.index) {
            AlTerminal.LINE_STR_TEXT -> {
                val escapedText = text.replace("%", "%%")
                KtExpressionLiteral(lineStringContent.line, "\"$escapedText\"")
            }
            AlTerminal.LINE_STR_ESCAPED_CHAR -> {
                if (text in listOf("\\b", "\\r")) {
                    throw LineException("illegal escape sequence $text", lineStringContent.line)
                }
                val escapedText = when (text) {
                    "\\$" -> "\$"
                    "\\'" -> "\'"
                    else -> text
                }
                KtExpressionLiteral(lineStringContent.line, "\"$escapedText\"")
            }
            AlTerminal.LINE_STR_REF -> {
                val identifier = text.drop(1)
                KtExpressionProperty(lineStringContent.line, identifier, null)
            }
            else -> throw LineException("line string content expected", lineStringContent.line)
        }
    }

    private fun joinExpressions(expressions: List<KtExpression>): List<KtExpression> {
        val joinedExpressions = ArrayList<KtExpression>()
        for (expression in expressions) {
            if (isStringLiteral(expression)) {
                val lastExpression = joinedExpressions.lastOrNull()
                if (lastExpression != null && isStringLiteral(lastExpression)) {
                    val joinedExpression = KtExpressionLiteral(
                        lastExpression.line,
                        (lastExpression as KtExpressionLiteral).string.dropLast(1)
                                + (expression as KtExpressionLiteral).string.drop(1)
                    )
                    joinedExpressions.removeLast()
                    joinedExpressions.add(joinedExpression)
                } else {
                    joinedExpressions.add(expression)
                }
            } else {
                joinedExpressions.add(expression)
            }
        }
        return joinedExpressions
    }

    private fun isStringLiteral(expression: KtExpression): Boolean {
        return expression is KtExpressionLiteral
                && expression.string.startsWith("\"")
                && expression.string.endsWith("\"")
    }
}
