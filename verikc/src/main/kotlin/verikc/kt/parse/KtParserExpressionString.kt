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
import verikc.base.ast.BaseType
import verikc.base.ast.LineException
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.*

object KtParserExpressionString {

    fun parse(stringLiteral: AlTree, symbolContext: SymbolContext): KtExpressionString {
        val segments = parseStringLiteral(stringLiteral, symbolContext)
        return KtExpressionString(
            stringLiteral.line,
            processSegments(segments)
        )
    }

    private fun parseStringLiteral(stringLiteral: AlTree, symbolContext: SymbolContext): List<KtStringSegment> {
        val lineStringLiteral = stringLiteral.find(AlRule.LINE_STRING_LITERAL)
        return lineStringLiteral.children.mapNotNull {
            when (it.index) {
                AlRule.LINE_STRING_CONTENT -> {
                    parseLineStringContent(it.unwrap())
                }
                AlRule.LINE_STRING_EXPRESSION -> {
                    KtStringSegmentExpression(
                        it.line,
                        BaseType.DEFAULT,
                        KtExpression(it.find(AlRule.EXPRESSION), symbolContext)
                    )
                }
                AlTerminal.QUOTE_OPEN -> null
                AlTerminal.QUOTE_CLOSE -> null
                else -> throw LineException("line string content or expression expected", it.line)
            }
        }
    }

    private fun parseLineStringContent(lineStringContent: AlTree): KtStringSegment {
        val text = lineStringContent.text
        return when (lineStringContent.index) {
            AlTerminal.LINE_STR_TEXT -> {
                KtStringSegmentLiteral(lineStringContent.line, text)
            }
            AlTerminal.LINE_STR_ESCAPED_CHAR -> {
                if (text in listOf("\\b", "\\r")) {
                    throw LineException("illegal escape sequence $text", lineStringContent.line)
                }
                return KtStringSegmentLiteral(
                    lineStringContent.line,
                    when (text) {
                        "\\$" -> "\$"
                        "\\'" -> "\'"
                        else -> text
                    }
                )
            }
            AlTerminal.LINE_STR_REF -> {
                val identifier = text.drop(1)
                return KtStringSegmentExpression(
                    lineStringContent.line,
                    BaseType.DEFAULT,
                    KtExpressionProperty(
                        lineStringContent.line,
                        identifier,
                        null
                    )
                )
            }
            else -> throw LineException("line string content expected", lineStringContent.line)
        }
    }

    private fun processSegments(segments: List<KtStringSegment>): List<KtStringSegment> {
        val processedSegments = ArrayList<KtStringSegment>()
        for (segment in segments) {
            val lastSegment = processedSegments.lastOrNull()
            when (segment) {
                is KtStringSegmentLiteral -> {
                    if (lastSegment is KtStringSegmentLiteral) {
                        val fusedSegment = KtStringSegmentLiteral(lastSegment.line, lastSegment.string + segment.string)
                        processedSegments.removeAt(processedSegments.size - 1)
                        processedSegments.add(fusedSegment)
                    } else {
                        processedSegments.add(segment)
                    }
                }
                is KtStringSegmentExpression -> {
                    val baseType = if (lastSegment is KtStringSegmentLiteral) {
                        when {
                            lastSegment.string.endsWith("0b", ignoreCase = true) -> BaseType.BIN
                            lastSegment.string.endsWith("0x", ignoreCase = true) -> BaseType.HEX
                            else -> BaseType.DEFAULT
                        }
                    } else BaseType.DEFAULT
                    processedSegments.add(KtStringSegmentExpression(segment.line, baseType, segment.expression))
                }
            }
        }
        return processedSegments
    }
}
