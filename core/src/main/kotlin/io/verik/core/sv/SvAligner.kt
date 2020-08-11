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
import java.lang.Integer.max

data class SvAlignerLine(
        override val line: Int,
        val tokens: List<String>
): Line

class SvAligner {

    companion object {

        fun build(lines:List<SvAlignerLine>, delimiter: String, endDelimiter: String, builder: SourceBuilder) {
            if (lines.isEmpty()) return
            val linesFiltered = filter(lines)
            val tokenCount = linesFiltered[0].tokens.size
            val spacing = Array(tokenCount) { Array(tokenCount) { 0 } }
            for (line in linesFiltered) {
                var startIndex = 0
                var length = 0
                for ((index, token) in line.tokens.withIndex()) {
                    if (token != "") {
                        spacing[startIndex][index] = max(spacing[startIndex][index], length)
                        startIndex = index
                        length = token.length + 1
                    }
                }
            }

            val startPos = Array(tokenCount) { 0 }
            for (i in 0 until tokenCount) {
                for (j  in 0 until i) {
                    startPos[i] = max(startPos[i], startPos[j] + spacing[j][i])
                }
            }

            for ((lineIndex, line) in linesFiltered.withIndex()) {
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
                builder.append(if (lineIndex < linesFiltered.lastIndex) delimiter else endDelimiter)
                builder.appendln()
            }
        }

        private fun filter(lines: List<SvAlignerLine>): List<SvAlignerLine> {
            val tokenCount = lines[0].tokens.size
            val empty = (0 until tokenCount).map { token -> (lines.map { it.tokens[token] }).all { it == "" } }
            return lines.map { line -> SvAlignerLine(line.line, line.tokens.filterIndexed { index, _ -> !empty[index] }) }
        }
    }
}