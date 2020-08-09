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

package io.verik.core.vk

import io.verik.core.LineException
import io.verik.core.al.AlRule
import io.verik.core.al.AlRuleType
import io.verik.core.al.AlToken
import io.verik.core.al.AlTokenType
import io.verik.core.sv.SvExpression
import io.verik.core.sv.SvExpressionCallable
import io.verik.core.sv.SvExpressionIdentifier
import io.verik.core.sv.SvExpressionString

sealed class VkStringSegment

data class VkStringSegmentLiteral(val string: String): VkStringSegment()

data class VkStringSegmentExpression(val expression: VkExpression): VkStringSegment()

class VkStringParser {

    companion object {

        fun parse(stringLiteral: AlRule): VkExpressionString {
            val lineStringLiteral = stringLiteral.firstAsRule()
            val segments = lineStringLiteral.children.map {
                if (it is AlRule) it
                else throw LineException("line string content or expression expected", stringLiteral.line)
            }.map { getSegment(it) }
            return VkExpressionString(stringLiteral.line, segments)
        }

        private fun getSegment(lineStringContentOrExpression: AlRule): VkStringSegment {
            return when (lineStringContentOrExpression.type) {
                AlRuleType.LINE_STRING_CONTENT -> {
                    getSegment(lineStringContentOrExpression.firstAsToken())
                }
                AlRuleType.LINE_STRING_EXPRESSION -> {
                    return VkStringSegmentExpression(VkExpression(lineStringContentOrExpression.firstAsRule()))
                }
                else -> throw LineException("line string content or expression expected", lineStringContentOrExpression.line)
            }
        }

        private fun getSegment(lineStringContent: AlToken): VkStringSegment {
            return when (lineStringContent.type) {
                AlTokenType.LINE_STR_TEXT -> {
                    VkStringSegmentLiteral(lineStringContent.text)
                }
                AlTokenType.LINE_STR_ESCAPED_CHAR -> {
                    listOf("\\u", "\\b", "\\r").forEach {
                        if (lineStringContent.text.startsWith(it)) {
                            throw LineException("illegal escape sequence ${lineStringContent.text}", lineStringContent.line)
                        }
                    }
                    when (lineStringContent.text ){
                        "\\$" -> VkStringSegmentLiteral("\$")
                        "\\'" -> VkStringSegmentLiteral("\'")
                        else -> VkStringSegmentLiteral(lineStringContent.text)
                    }
                }
                AlTokenType.LINE_STR_REF -> {
                    val identifier = lineStringContent.text.drop(1)
                    return VkStringSegmentExpression(VkExpressionIdentifier(lineStringContent.line, identifier))
                }
                else -> throw LineException("line string content expected", lineStringContent.line)
            }
        }
    }
}

class VkStringExtractor {

    companion object {

        fun extract(expressionString: VkExpressionString): SvExpression {
            return if (expressionString.segments.all { it is VkStringSegmentLiteral }) {
                SvExpressionString(expressionString.line, expressionString.segments
                        .joinToString(separator = "") { (it as VkStringSegmentLiteral).string })
            } else {
                val formatString = expressionString.segments
                        .joinToString(separator = "") {
                            when (it) {
                                is VkStringSegmentLiteral -> it.string
                                is VkStringSegmentExpression -> "0x%x"
                            }
                        }
                val expressions = expressionString.segments
                        .mapNotNull {
                            if (it is VkStringSegmentExpression) it
                            else null
                        }
                        .map { it.expression.extractExpression() }
                return SvExpressionCallable(expressionString.line,
                        SvExpressionIdentifier(expressionString.line, "\$sformatf"),
                        listOf(SvExpressionString(expressionString.line, formatString)) + expressions)
            }
        }
    }
}
