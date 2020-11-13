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
import verikc.base.ast.LineException

data class SvAlignedLine(
        override val line: Int,
        val tokens: List<String>
): Line

data class SvAlignedBlock(
        val lines: List<SvAlignedLine>,
        val count: Int,
        val midDelimiter: String,
        val endDelimiter: String
): SvBuildable {

    override fun build(builder: SvSourceBuilder) {
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

        operator fun invoke(lines: List<SvAlignedLine>, midDelimiter: String, endDelimiter: String): SvAlignedBlock {
            if (lines.isEmpty()) {
                throw IllegalArgumentException("aligned block has no lines")
            }
            val count = lines[0].tokens.size
            lines.forEach {
                if (it.tokens.size != count) {
                    throw LineException("aligned line token count mismatch", it.line)
                }
            }
            return SvAlignedBlock(
                    filter(lines, count),
                    count,
                    midDelimiter,
                    endDelimiter
            )
        }

        private fun filter(lines: List<SvAlignedLine>, count: Int): List<SvAlignedLine> {
            val empty = (0 until count).map { token ->
                (lines.map { it.tokens[token] }).all { it == "" }
            }
            return lines.map { line ->
                SvAlignedLine(
                        line.line,
                        line.tokens.filterIndexed { index, _ -> !empty[index] }
                )
            }
        }
    }
}
