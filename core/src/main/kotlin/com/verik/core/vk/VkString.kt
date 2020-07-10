package com.verik.core.vk

import com.verik.core.LinePos
import com.verik.core.kt.*
import com.verik.core.sv.SvCallableExpression
import com.verik.core.sv.SvExpression
import com.verik.core.sv.SvIdentifierExpression
import com.verik.core.sv.SvStringExpression

// Copyright (c) 2020 Francis Wang

sealed class VkStringSegment

data class VkStringSegmentLiteral(val string: String): VkStringSegment()

data class VkStringSegmentExpression(val expression: VkExpression): VkStringSegment()

class VkStringParser {

    companion object {

        fun parse(stringLiteral: KtRule): VkStringExpression {
            val lineStringLiteral = stringLiteral.firstAsRule()
            val segments = lineStringLiteral.children.map {
                if (it is KtRule) it
                else throw KtGrammarException("line string content or expression expected", stringLiteral.linePos)
            }.map { getSegment(it) }
            return VkStringExpression(stringLiteral.linePos, segments)
        }

        private fun getSegment(lineStringContentOrExpression: KtRule): VkStringSegment {
            return when (lineStringContentOrExpression.type) {
                KtRuleType.LINE_STRING_CONTENT -> {
                    getSegment(lineStringContentOrExpression.firstAsToken())
                }
                KtRuleType.LINE_STRING_EXPRESSION -> {
                    return VkStringSegmentExpression(VkExpression(lineStringContentOrExpression.firstAsRule()))
                }
                else -> throw KtGrammarException("line string content or expression expected", lineStringContentOrExpression.linePos)
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
                            throw VkParseException("illegal escape sequence ${lineStringContent.text}", lineStringContent.linePos)
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
                    val linePos = LinePos(lineStringContent.linePos.line, lineStringContent.linePos.pos + 1)
                    return VkStringSegmentExpression(VkIdentifierExpression(linePos, identifier))
                }
                else -> throw KtGrammarException("line string content expected", lineStringContent.linePos)
            }
        }
    }
}

class VkStringExtractor {

    companion object {

        fun extract(stringExpression: VkStringExpression): SvExpression {
            return if (stringExpression.segments.all { it is VkStringSegmentLiteral }) {
                SvStringExpression(stringExpression.linePos, stringExpression.segments
                        .joinToString(separator = "") { (it as VkStringSegmentLiteral).string })
            } else {
                val formatString = stringExpression.segments
                        .joinToString(separator = "") {
                            when (it) {
                                is VkStringSegmentLiteral -> it.string
                                is VkStringSegmentExpression -> "%X"
                            }
                        }
                val expressions = stringExpression.segments
                        .mapNotNull {
                            if (it is VkStringSegmentExpression) it
                            else null
                        }
                        .map { it.expression.extractExpression() }
                return SvCallableExpression(stringExpression.linePos,
                        SvIdentifierExpression(stringExpression.linePos, "\$sformatf"),
                        listOf(SvStringExpression(stringExpression.linePos, formatString)) + expressions)
            }
        }
    }
}
