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

package io.verik.core.svx

import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.main.SourceBuilder

data class SvxAlignedLine(
        override val line: Int,
        val tokens: List<String>
): Line

data class SvxAlignedBlock(
        val lines: List<SvxAlignedLine>,
        val count: Int,
        val midDelimiter: String,
        val endDelimiter: String
): SvxBuildable {

    override fun build(builder: SourceBuilder) {
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

    companion object {

        operator fun invoke(lines: List<SvxAlignedLine>, midDelimiter: String, endDelimiter: String): SvxAlignedBlock {
            if (lines.isEmpty()) {
                throw IllegalArgumentException("aligned block has no lines")
            }
            val count = lines[0].tokens.size
            lines.forEach {
                if (it.tokens.size != count) {
                    throw LineException("aligned line token count mismatch", it.line)
                }
            }
            return SvxAlignedBlock(
                    filter(lines, count),
                    count,
                    midDelimiter,
                    endDelimiter
            )
        }

        private fun filter(lines: List<SvxAlignedLine>, count: Int): List<SvxAlignedLine> {
            val empty = (0 until count).map { token ->
                (lines.map { it.tokens[token] }).all { it == "" }
            }
            return lines.map { line ->
                SvxAlignedLine(
                        line.line,
                        line.tokens.filterIndexed { index, _ -> !empty[index] }
                )
            }
        }
    }
}
