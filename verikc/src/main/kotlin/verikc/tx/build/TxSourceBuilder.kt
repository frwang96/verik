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

package verikc.tx.build

import verikc.base.ast.Line

inline fun indent(builder: TxSourceBuilder, block: () -> Unit) {
    builder.indent++
    block()
    builder.indent--
}

class TxSourceBuilder private constructor(
    private val labelLines: Boolean,
) {

    private val labelLength = 4
    private val indentLength = 4
    private val maxLineLength = 120

    private val sourceBuilder = StringBuilder()
    private val lineBuilder = StringBuilder()
    private val tokenizer = Tokenizer()
    private var line: Int? = null
    private var isNewLine = true
    var indent = 0

    constructor(): this(false)

    constructor(labelLines: Boolean, fileHeader: String): this(labelLines) {
        sourceBuilder.appendLine(fileHeader)

        if (labelLines) {
            sourceBuilder.appendLine("`define _(N)")
            sourceBuilder.appendLine()
        }
    }

    fun label(line: Line) {
        if (lineBuilder.isNotEmpty() || !isNewLine)
            throw IllegalArgumentException("label should be set at start of line")
        this.line = line.line
    }

    fun append(string: String) {
        string.chars().forEach { appendChar(it) }
    }

    fun appendln(string: String) {
        string.chars().forEach { appendChar(it) }
        appendChar('\n'.toInt())
    }

    fun appendln() {
        appendChar('\n'.toInt())
    }

    override fun toString(): String {
        return sourceBuilder.toString()
    }

    private fun appendChar(char: Int) {
        val token = tokenizer.appendChar(char)
        if (token != null) appendToken(token)
        if (char == '\n'.toInt()) {
            flushLine(true)
        }
    }

    private fun appendToken(token: String) {
        if (lineBuilder.isEmpty()) {
            lineBuilder.append(token)
        } else {
            val lineLength = totalIndentLength() + lineBuilder.length + token.length
            if (lineLength > maxLineLength) flushLine(false)
            lineBuilder.append(token)
        }
    }

    private fun flushLine(startNewLine: Boolean) {
        if (labelLines) {
            if (isNewLine) {
                sourceBuilder.append(labelString(line) + " ".repeat(indentLength))
                line = null
            } else {
                sourceBuilder.append(labelString(null) + " ".repeat(indentLength))
            }
        }

        val lineString = lineBuilder.toString().trim()
        lineBuilder.clear()
        if (lineString != "") {
            sourceBuilder.append(" ".repeat(totalIndentLength()))
            sourceBuilder.append(lineString)
        }
        sourceBuilder.appendLine()
        isNewLine = startNewLine
    }

    private fun totalIndentLength(): Int {
        return indentLength * (if (isNewLine) indent else indent + 1)
    }

    private fun labelString(line: Int?): String {
        return if (line != null) {
            "`_( ${line.toString().padStart(labelLength, ' ')} )"
        } else {
            "`_( ${" ".repeat(labelLength)} )"
        }
    }

    private class Tokenizer {

        private val tokenBuilder = StringBuilder()
        private var isStringLiteral = false

        fun appendChar(char: Int): String? {
            if (char == '\"'.toInt()) {
                if (tokenBuilder.isEmpty() || tokenBuilder.last() != '\\') {
                    isStringLiteral = !isStringLiteral
                }
            }
            tokenBuilder.appendCodePoint(char)
            return if (!isStringLiteral && char in listOf('(', ' ', '\n').map { it.toInt() }) {
                val token = tokenBuilder.toString()
                tokenBuilder.clear()
                token
            } else {
                null
            }
        }
    }
}
