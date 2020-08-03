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

import io.verik.core.FileLineException
import io.verik.core.kt.KtRule
import io.verik.core.kt.KtRuleType
import io.verik.core.kt.KtToken
import io.verik.core.kt.KtTokenType
import io.verik.core.sv.SvCallableExpression
import io.verik.core.sv.SvExpression
import io.verik.core.sv.SvIdentifierExpression
import io.verik.core.sv.SvStringExpression

sealed class VkStringSegment

data class VkStringSegmentLiteral(val string: String): VkStringSegment()

data class VkStringSegmentExpression(val expression: VkExpression): VkStringSegment()

class VkStringParser {

    companion object {

        fun parse(stringLiteral: KtRule): VkStringExpression {
            val lineStringLiteral = stringLiteral.firstAsRule()
            val segments = lineStringLiteral.children.map {
                if (it is KtRule) it
                else throw FileLineException("line string content or expression expected", stringLiteral.fileLine)
            }.map { getSegment(it) }
            return VkStringExpression(stringLiteral.fileLine, segments)
        }

        private fun getSegment(lineStringContentOrExpression: KtRule): VkStringSegment {
            return when (lineStringContentOrExpression.type) {
                KtRuleType.LINE_STRING_CONTENT -> {
                    getSegment(lineStringContentOrExpression.firstAsToken())
                }
                KtRuleType.LINE_STRING_EXPRESSION -> {
                    return VkStringSegmentExpression(VkExpression(lineStringContentOrExpression.firstAsRule()))
                }
                else -> throw FileLineException("line string content or expression expected", lineStringContentOrExpression.fileLine)
            }
        }

        private fun getSegment(lineStringContent: KtToken): VkStringSegment {
            return when (lineStringContent.type) {
                KtTokenType.LINE_STR_TEXT -> {
                    VkStringSegmentLiteral(lineStringContent.text)
                }
                KtTokenType.LINE_STR_ESCAPED_CHAR -> {
                    listOf("\\u", "\\b", "\\r").forEach {
                        if (lineStringContent.text.startsWith(it)) {
                            throw FileLineException("illegal escape sequence ${lineStringContent.text}", lineStringContent.fileLine)
                        }
                    }
                    when (lineStringContent.text ){
                        "\\$" -> VkStringSegmentLiteral("\$")
                        "\\'" -> VkStringSegmentLiteral("\'")
                        else -> VkStringSegmentLiteral(lineStringContent.text)
                    }
                }
                KtTokenType.LINE_STR_REF -> {
                    val identifier = lineStringContent.text.drop(1)
                    return VkStringSegmentExpression(VkIdentifierExpression(lineStringContent.fileLine, identifier))
                }
                else -> throw FileLineException("line string content expected", lineStringContent.fileLine)
            }
        }
    }
}

class VkStringExtractor {

    companion object {

        fun extract(stringExpression: VkStringExpression): SvExpression {
            return if (stringExpression.segments.all { it is VkStringSegmentLiteral }) {
                SvStringExpression(stringExpression.fileLine, stringExpression.segments
                        .joinToString(separator = "") { (it as VkStringSegmentLiteral).string })
            } else {
                val formatString = stringExpression.segments
                        .joinToString(separator = "") {
                            when (it) {
                                is VkStringSegmentLiteral -> it.string
                                is VkStringSegmentExpression -> "0x%x"
                            }
                        }
                val expressions = stringExpression.segments
                        .mapNotNull {
                            if (it is VkStringSegmentExpression) it
                            else null
                        }
                        .map { it.expression.extractExpression() }
                return SvCallableExpression(stringExpression.fileLine,
                        SvIdentifierExpression(stringExpression.fileLine, "\$sformatf"),
                        listOf(SvStringExpression(stringExpression.fileLine, formatString)) + expressions)
            }
        }
    }
}
