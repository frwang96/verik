package com.verik.core.sv

import com.verik.core.SourceBuilder
import java.lang.Integer.max

// Copyright (c) 2020 Francis Wang

class SvAligner {

    companion object {
        fun build(lines:List<List<String>>, delimiter: String, endDelimiter: String, builder: SourceBuilder) {
            if (lines.isEmpty()) return
            val linesFiltered = filter(lines)
            val segments = linesFiltered[0].size
            val spacing = Array(segments) { Array(segments) { 0 } }
            for (line in linesFiltered) {
                var startIndex = 0
                var length = 0
                for ((index, segment) in line.withIndex()) {
                    if (segment != "") {
                        spacing[startIndex][index] = max(spacing[startIndex][index], length)
                        startIndex = index
                        length = segment.length + 1
                    }
                }
            }

            val startPos = Array(segments) { 0 }
            for (i in 0 until segments) {
                for (j  in 0 until i) {
                    startPos[i] = max(startPos[i], startPos[j] + spacing[j][i])
                }
            }

            for ((lineIndex, line) in linesFiltered.withIndex()) {
                var pos = 0
                for ((segmentIndex, segment) in line.withIndex()) {
                    if (segment != "") {
                        if (startPos[segmentIndex] > pos) {
                            builder.append(" ".repeat(startPos[segmentIndex] - pos))
                        }
                        builder.append(segment)
                        pos = startPos[segmentIndex] + segment.length
                    }
                }
                builder.append(if (lineIndex < linesFiltered.lastIndex) delimiter else endDelimiter)
                builder.appendln()
            }
        }

        private fun filter(lines: List<List<String>>): List<List<String>> {
            val segments = lines[0].size
            val empty = (0 until segments).map { segment -> (lines.map { it[segment] }).all { it == "" } }
            return lines.map { line -> line.filterIndexed { index, _ -> !empty[index] } }
        }
    }
}