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

package io.verik.core.sv

import io.verik.core.main.Line
import io.verik.core.main.SourceBuilder

data class SvxAlignerLine(
        override val line: Int,
        val tokens: List<String>
): Line

object SvxAligner {

    fun build(
            lines:List<SvxAlignerLine>,
            midDelimiter: String,
            endDelimiter: String,
            builder: SourceBuilder
    ) {
        if (lines.isNotEmpty()) {
            val count = lines[0].tokens.size
            if (lines.any { it.tokens.size != count }) {
                throw IllegalArgumentException("aligner line token count mismatch")
            }
            val filteredLines = filter(lines, count)
            buildFiltered(filteredLines, count, midDelimiter, endDelimiter, builder)
        }
    }

    private fun buildFiltered(
            lines:List<SvxAlignerLine>,
            count: Int,
            midDelimiter: String,
            endDelimiter: String,
            builder: SourceBuilder
    ) {
        // compute spacing matrix
        val spacing = Array(count) { Array(count) { 0 } }
        for (line in lines) {
            var startIndex = 0
            var length = 0
            for ((index, token) in line.tokens.withIndex()) {
                if (token != "") {
                    spacing[startIndex][index] = Integer.max(spacing[startIndex][index], length)
                    startIndex = index
                    length = token.length + 1
                }
            }
        }

        // compute start positions
        val startPos = Array(count) { 0 }
        for (i in 0 until count) {
            for (j  in 0 until i) {
                startPos[i] = Integer.max(startPos[i], startPos[j] + spacing[j][i])
            }
        }

        for ((lineIndex, line) in lines.withIndex()) {
            builder.label(line)
            var pos = 0
            for ((tokenIndex, token) in line.tokens.withIndex()) {
                if (token != "") {
                    if (startPos[tokenIndex] > pos) {
                        builder.append(" ".repeat(startPos[tokenIndex] - pos))
                    }
                    builder.append(token)
                    pos = startPos[tokenIndex] + token.length
                }
            }
            val delimiter = if (lineIndex < lines.lastIndex) {
                midDelimiter
            } else {
                endDelimiter
            }
            builder.append(delimiter)
            builder.appendln()
        }
    }

    private fun filter(lines: List<SvxAlignerLine>, count: Int): List<SvxAlignerLine> {
        val empty = (0 until count).map { token ->
            (lines.map { it.tokens[token] }).all { it == "" }
        }
        return lines.map { line ->
            SvxAlignerLine(
                    line.line,
                    line.tokens.filterIndexed { index, _ -> !empty[index] }
            )
        }
    }
}