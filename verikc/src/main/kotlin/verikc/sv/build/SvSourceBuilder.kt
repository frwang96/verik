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

package verikc.sv.build

import verikc.base.ast.Line
import java.util.stream.IntStream

inline fun indent(builder: SvSourceBuilder, block: () -> Unit) {
    builder.indent++
    block()
    builder.indent--
}

class SvSourceBuilder private constructor(
    private val labelLines: Boolean,
) {

    private val labelLength = 4
    private val sourceBuilder = StringBuilder()
    private val lineBuilder = StringBuilder()
    private var line: Int? = null
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
        this.line = line.line
    }

    fun append(string: String) {
        appendStream(string.chars())
    }

    fun appendln(string: String) {
        appendStream(string.chars())
        appendln()
    }

    fun appendln() {
        appendStream(IntStream.of('\n'.toInt()))
    }

    override fun toString(): String {
        return sourceBuilder.toString()
    }

    private fun appendStream(chars: IntStream) {
        for (char in chars) {
            if (char != '\n'.toInt()) {
                lineBuilder.appendCodePoint(char)
            } else {
                val lineString = lineBuilder.toString()
                lineBuilder.clear()
                if (labelLines) sourceBuilder.append("${labelString()}    ")
                if (lineString != "") {
                    sourceBuilder.append("    ".repeat(indent))
                    sourceBuilder.append(lineString)
                }
                sourceBuilder.append("\n")
            }
        }
    }

    private fun labelString(): String {
        val label = if (line != null) {
            line.toString().padStart(labelLength, ' ')
        } else {
            " ".repeat(labelLength)
        }
        line = null
        return "`_( $label )"
    }
}
