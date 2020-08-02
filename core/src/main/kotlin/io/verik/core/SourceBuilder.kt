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

package io.verik.core

import java.util.stream.IntStream

inline fun indent(builder: SourceBuilder, block: () -> Unit) {
    builder.indent++
    block()
    builder.indent--
}

class SourceBuilder private constructor(private val labelLines: Boolean, private val labelLength: Int) {

    private val builder = StringBuilder()
    private var newLine = true
    private var line: Int? = null
    var indent = 0

    constructor(): this(false, 0)

    constructor(labelLines: Boolean, labelLength: Int, fileHeader: String): this(labelLines, labelLength) {
        builder.appendln(fileHeader)

        if (labelLines) {
            builder.appendln("`define _(N)")
            builder.appendln()
        }
    }

    fun label(line: Int) {
        this.line = line
    }

    fun append(string: String) {
        appendStream(string.chars())
    }

    fun appendln(string: String) {
        appendStream(string.chars())
        appendStream(IntStream.of('\n'.toInt()))
    }

    fun appendln() {
        appendStream(IntStream.of('\n'.toInt()))
    }

    override fun toString() = builder.toString()

    private fun appendStream(chars: IntStream) {
        for (char in chars) {
            if (char == '\n'.toInt()) {
                if (newLine && labelLines) builder.append(labelString())
                builder.append("\n")
                newLine = true
            } else {
                if (newLine) {
                    if (labelLines) builder.append("${labelString()}    ")
                    builder.append("  ".repeat(indent))
                    newLine = false
                }
                builder.appendCodePoint(char)
            }
        }
    }

    private fun labelString(): String {
        val label = if (line != null) {
            "`_(${line.toString().padStart(labelLength, '0')})"
        } else {
            "`_(${" ".repeat(labelLength)})"
        }
        line = null
        return label
    }
}