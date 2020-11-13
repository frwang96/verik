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

import verikc.al.*
import verikc.base.*
import verikc.base.ast.*
import verikc.kt.ast.*

object KtParserExpressionString {

    fun parse(stringLiteral: AlRule, indexer: SymbolIndexer): KtExpressionString {
        val segments = parseStringLiteral(stringLiteral, indexer)
        return KtExpressionString(
                stringLiteral.line,
                null,
                fuseSegments(segments)
        )
    }

    private fun parseStringLiteral(stringLiteral: AlRule, indexer: SymbolIndexer): List<KtStringSegment> {
        val lineStringLiteral = stringLiteral.childAs(AlRuleType.LINE_STRING_LITERAL)
        return lineStringLiteral.children.map {
            val lineStringSegment = it.asRule()
            when (lineStringSegment.type) {
                AlRuleType.LINE_STRING_CONTENT -> {
                    parseLineStringContent(lineStringSegment.firstAsToken())
                }
                AlRuleType.LINE_STRING_EXPRESSION -> {
                    KtStringSegmentExpression(
                            it.line,
                            BaseType.DEFAULT,
                            KtExpression(lineStringSegment.firstAsRule(), indexer)
                    )
                }
                else -> throw LineException("line string content or expression expected", lineStringSegment)
            }
        }
    }

    private fun parseLineStringContent(lineStringContent: AlToken): KtStringSegment {
        return when (lineStringContent.type) {
            AlTokenType.LINE_STR_TEXT -> {
                KtStringSegmentLiteral(lineStringContent.line, lineStringContent.text)
            }
            AlTokenType.LINE_STR_ESCAPED_CHAR -> {
                if (lineStringContent.text in listOf("\\b", "\\r")) {
                    throw LineException("illegal escape sequence ${lineStringContent.text}", lineStringContent)
                }
                return KtStringSegmentLiteral(
                        lineStringContent.line,
                        when (lineStringContent.text){
                            "\\$" -> "\$"
                            "\\'" -> "\'"
                            else -> lineStringContent.text
                        }
                )
            }
            AlTokenType.LINE_STR_REF -> {
                val identifier = lineStringContent.text.drop(1)
                return KtStringSegmentExpression(
                        lineStringContent.line,
                        BaseType.DEFAULT,
                        KtExpressionProperty(
                                lineStringContent.line,
                                null,
                                identifier,
                                null,
                                null
                        )
                )
            }
            else -> throw LineException("line string content expected", lineStringContent)
        }
    }

    private fun fuseSegments(segments: List<KtStringSegment>): List<KtStringSegment> {
        val fusedSegments = ArrayList<KtStringSegment>()
        for (segment in segments) {
            val lastSegment = fusedSegments.lastOrNull()
            when (segment) {
                is KtStringSegmentLiteral -> {
                    if (lastSegment is KtStringSegmentLiteral) {
                        val fusedSegment = KtStringSegmentLiteral(lastSegment.line, lastSegment.string + segment.string)
                        fusedSegments.removeAt(fusedSegments.size - 1)
                        fusedSegments.add(fusedSegment)
                    } else {
                        fusedSegments.add(segment)
                    }
                }
                is KtStringSegmentExpression -> {
                    if (lastSegment is KtStringSegmentLiteral) {
                        val fusedSegment = when {
                            lastSegment.string.endsWith("0b", ignoreCase = true) -> KtStringSegmentExpression(
                                    segment.line,
                                    BaseType.BIN,
                                    segment.expression
                            )
                            lastSegment.string.endsWith("0x", ignoreCase = true) -> KtStringSegmentExpression(
                                    segment.line,
                                    BaseType.HEX,
                                    segment.expression
                            )
                            else -> null
                        }
                        if (fusedSegment != null) {
                            fusedSegments.removeAt(fusedSegments.size - 1)
                            val shortenedString = lastSegment.string.dropLast(2)
                            if (shortenedString.isNotEmpty()) {
                                fusedSegments.add(KtStringSegmentLiteral(lastSegment.line, shortenedString))
                            }
                            fusedSegments.add(fusedSegment)
                        } else {
                            fusedSegments.add(segment)
                        }
                    } else {
                        fusedSegments.add(segment)
                    }
                }
            }
        }
        return fusedSegments
    }
}
