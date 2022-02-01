/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.serialize.general.FileHeaderBuilder

class SourceBuilder(
    projectContext: ProjectContext,
    private val file: EFile
) {

    private val labelLines = projectContext.config.labelLines
    private val enableLineDirective = projectContext.config.enableLineDirective
    private val indentLength = projectContext.config.indentLength
    private val wrapLength = projectContext.config.wrapLength
    private val labelLength = if (labelLines) 12 else 0

    private val sourceBuilder = StringBuilder()

    init {
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            file.inputPath,
            file.outputPath,
            FileHeaderBuilder.HeaderStyle.SYSTEM_VERILOG
        )
        sourceBuilder.append(fileHeader)

        if (labelLines) {
            if (enableLineDirective) {
                val pathString = Platform.getStringFromPath(file.inputPath.toAbsolutePath())
                sourceBuilder.appendLine(
                    """
                        `ifdef _
                        `undef _
                        `endif
                        `define _(n) `line n "$pathString" 0 \
                    """.trimIndent()
                )
                sourceBuilder.appendLine()
            } else {
                sourceBuilder.appendLine(
                    """
                        `ifndef _
                        `define _(n)
                        `endif
                    """.trimIndent()
                )
                sourceBuilder.appendLine()
            }
        }
    }

    fun toTextFile(): TextFile {
        return TextFile(file.outputPath, sourceBuilder.toString())
    }

    fun build(sourceActionLines: List<SourceActionLine>) {
        val alignments = ArrayList(sourceActionLines.map { getAlignment(it) })
        matchAlignment(alignments)
        sourceActionLines.zip(alignments).forEach { (sourceActionLine, alignment) ->
            buildLine(sourceActionLine, alignment)
        }
    }

    private fun getAlignment(sourceActionLine: SourceActionLine): Int? {
        var alignment = labelLength + sourceActionLine.indents * indentLength
        sourceActionLine.sourceActions.forEach {
            alignment += when (it.type) {
                SourceActionType.REGULAR -> it.content.length
                SourceActionType.SOFT_BREAK -> 0
                SourceActionType.HARD_BREAK -> 1
                SourceActionType.ALIGN -> return alignment
            }
        }
        return null
    }

    private fun matchAlignment(alignments: ArrayList<Int?>) {
        var index = 0
        while (index < alignments.size) {
            val alignment = alignments[index]
            if (alignment != null) {
                var matchedAlignment: Int = alignment
                var endIndex = index + 1
                while (endIndex < alignments.size) {
                    val endAlignment = alignments[endIndex]
                    if (endAlignment != null) {
                        if (endAlignment > matchedAlignment)
                            matchedAlignment = endAlignment
                        endIndex++
                    } else break
                }
                while (index < endIndex) {
                    alignments[index] = matchedAlignment
                    index++
                }
            } else {
                index++
            }
        }
    }

    private fun buildLine(sourceActionLine: SourceActionLine, alignment: Int?) {
        labelLine(sourceActionLine)
        if (sourceActionLine.sourceActions.isEmpty()) {
            sourceBuilder.appendLine()
            return
        }

        sourceBuilder.append(" ".repeat(sourceActionLine.indents * indentLength))
        var index = 0
        var lineLength = labelLength + sourceActionLine.indents * indentLength

        // no wrap before alignment
        if (alignment != null) {
            while (index < sourceActionLine.sourceActions.size) {
                val sourceAction = sourceActionLine.sourceActions[index]
                when (sourceAction.type) {
                    SourceActionType.REGULAR -> {
                        sourceBuilder.append(sourceAction.content)
                        lineLength += sourceAction.content.length
                    }
                    SourceActionType.SOFT_BREAK -> {}
                    SourceActionType.HARD_BREAK -> {
                        sourceBuilder.append(" ")
                        lineLength += 1
                    }
                    SourceActionType.ALIGN -> {
                        sourceBuilder.append(" ".repeat(alignment - lineLength))
                        lineLength = alignment
                        index++
                        break
                    }
                }
                index++
            }
        }

        // wrap after alignment
        while (index < sourceActionLine.sourceActions.size) {
            val sourceAction = sourceActionLine.sourceActions[index]
            when (sourceAction.type) {
                SourceActionType.REGULAR -> {
                    sourceBuilder.append(sourceAction.content)
                    lineLength += sourceAction.content.length
                }
                SourceActionType.SOFT_BREAK -> {
                    if (isWrap(sourceActionLine.sourceActions, index + 1, lineLength)) {
                        sourceBuilder.appendLine()
                        labelLine(sourceActionLine, index + 1)
                        sourceBuilder.append(" ".repeat((sourceActionLine.indents + 1) * indentLength))
                        lineLength = labelLength + (sourceActionLine.indents + 1) * indentLength
                    }
                }
                SourceActionType.HARD_BREAK -> {
                    if (isWrap(sourceActionLine.sourceActions, index + 1, lineLength + 1)) {
                        sourceBuilder.appendLine()
                        labelLine(sourceActionLine, index + 1)
                        sourceBuilder.append(" ".repeat((sourceActionLine.indents + 1) * indentLength))
                        lineLength = labelLength + (sourceActionLine.indents + 1) * indentLength
                    } else {
                        sourceBuilder.append(" ")
                        lineLength += 1
                    }
                }
                SourceActionType.ALIGN -> {}
            }
            index++
        }
        sourceBuilder.appendLine()
    }

    private fun labelLine(sourceActionLine: SourceActionLine, index: Int = 0) {
        if (labelLines) {
            if (index < sourceActionLine.sourceActions.size) {
                val line = sourceActionLine.sourceActions[index].location.line
                val lineString = "$line".padStart(6, ' ')
                sourceBuilder.append("`_($lineString)  ")
            }
        }
    }

    private fun isWrap(sourceActions: ArrayList<SourceAction>, offset: Int, lineLength: Int): Boolean {
        var index = offset
        var endLineLength = lineLength
        while (index < sourceActions.size) {
            val sourceAction = sourceActions[index]
            when (sourceAction.type) {
                SourceActionType.REGULAR -> {
                    endLineLength += sourceAction.content.length
                }
                SourceActionType.SOFT_BREAK -> break
                SourceActionType.HARD_BREAK -> break
                SourceActionType.ALIGN -> {}
            }
            index++
        }
        return endLineLength > wrapLength
    }
}
