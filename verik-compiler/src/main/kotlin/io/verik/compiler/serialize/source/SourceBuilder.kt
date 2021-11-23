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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.serialize.general.FileHeaderBuilder
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

class SourceBuilder(
    private val projectContext: ProjectContext,
    private val file: EFile
) {

    private var sourceActionLine = SourceActionLine(0, ArrayList())
    private val sourceActionLines = ArrayList<SourceActionLine>()
    private val locationStack = ArrayDeque<SourceLocation>()
    private var indent = 0

    fun getResult(): SourceBuilderResult {
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Serialized source must end with a new line")

        val outputPath = file.getOutputPathNotNull()
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            file.inputPath,
            outputPath,
            FileHeaderBuilder.HeaderStyle.SV
        )
        val sourceBuilder = StringBuilder()
        sourceBuilder.append(fileHeader)

        if (projectContext.config.labelLines) {
            val outputPathLabeled = outputPath.parent.resolve("${outputPath.fileName}.labeled")
            val fileHeaderLabeled = FileHeaderBuilder.build(
                projectContext,
                file.inputPath,
                outputPathLabeled,
                FileHeaderBuilder.HeaderStyle.SV
            )
            val sourceBuilderLabeled = StringBuilder()
            sourceBuilderLabeled.append(fileHeaderLabeled)

            val sourceActionBuilder = SourceActionBuilder(
                sourceBuilder,
                sourceBuilderLabeled,
                projectContext.config.wrapLength,
                projectContext.config.indentLength
            )
            sourceActionBuilder.build(sourceActionLines)
            val textFile = TextFile(outputPath, sourceBuilder.toString())
            val textFileLabeled = TextFile(outputPathLabeled, sourceBuilderLabeled.toString())
            return SourceBuilderResult(textFile, textFileLabeled)
        } else {
            val sourceActionBuilder = SourceActionBuilder(
                sourceBuilder,
                null,
                projectContext.config.wrapLength,
                projectContext.config.indentLength
            )
            sourceActionBuilder.build(sourceActionLines)
            val textFile = TextFile(outputPath, sourceBuilder.toString())
            return SourceBuilderResult(textFile, null)
        }
    }

    fun label(element: EElement, block: () -> Unit) {
        locationStack.push(element.location)
        block()
        locationStack.pop()
    }

    fun indent(block: () -> Unit) {
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Indent in must start at a new line")
        sourceActionLine = SourceActionLine(++indent, ArrayList())
        block()
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Indent out must start at a new line")
        sourceActionLine = SourceActionLine(--indent, ArrayList())
    }

    fun appendLine(content: String) {
        append(content)
        appendLine()
    }

    fun appendLine() {
        sourceActionLines.add(sourceActionLine)
        sourceActionLine = SourceActionLine(indent, ArrayList())
    }

    fun append(content: String) {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.REGULAR, content, locationStack.peek()!!))
    }

    fun softBreak() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.SOFT_BREAK, "", locationStack.peek()!!))
    }

    fun hardBreak() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.HARD_BREAK, "", locationStack.peek()!!))
    }

    fun align() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.ALIGN, "", locationStack.peek()!!))
    }

    private enum class SourceActionType { REGULAR, SOFT_BREAK, HARD_BREAK, ALIGN }

    private data class SourceAction(val type: SourceActionType, val content: String, val location: SourceLocation)

    private data class SourceActionLine(val indents: Int, val sourceActions: ArrayList<SourceAction>)

    private class SourceActionBuilder(
        private val sourceBuilder: StringBuilder,
        private val sourceBuilderLabeled: StringBuilder?,
        private val wrapLength: Int,
        private val indentLength: Int
    ) {

        private val locationLabels = ArrayList<LocationLabel>()

        fun build(sourceActionLines: ArrayList<SourceActionLine>) {
            val alignments = ArrayList(sourceActionLines.map { getAlignment(it) })
            matchAlignment(alignments)
            sourceActionLines.zip(alignments).forEach { (sourceActionLine, alignment) ->
                buildLine(sourceActionLine, alignment)
            }
        }

        private fun getAlignment(sourceActionLine: SourceActionLine): Int? {
            var alignment = sourceActionLine.indents * indentLength
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
            var index = 0
            if (sourceActionLine.sourceActions.isEmpty()) {
                appendLine()
                return
            }
            append(" ".repeat(sourceActionLine.indents * indentLength), sourceActionLine.sourceActions[0].location)
            var lineLength = sourceActionLine.indents * indentLength

            // no wrap before alignment
            if (alignment != null) {
                while (index < sourceActionLine.sourceActions.size) {
                    val sourceAction = sourceActionLine.sourceActions[index]
                    when (sourceAction.type) {
                        SourceActionType.REGULAR -> {
                            append(sourceAction.content, sourceAction.location)
                            lineLength += sourceAction.content.length
                        }
                        SourceActionType.SOFT_BREAK -> {}
                        SourceActionType.HARD_BREAK -> {
                            append(" ", sourceAction.location)
                            lineLength += 1
                        }
                        SourceActionType.ALIGN -> {
                            append(" ".repeat(alignment - lineLength), sourceAction.location)
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
                        append(sourceAction.content, sourceAction.location)
                        lineLength += sourceAction.content.length
                    }
                    SourceActionType.SOFT_BREAK -> {
                        if (isWrap(sourceActionLine.sourceActions, index + 1, lineLength)) {
                            appendLine()
                            append(" ".repeat((sourceActionLine.indents + 1) * indentLength), sourceAction.location)
                            lineLength = (sourceActionLine.indents + 1) * indentLength
                        }
                    }
                    SourceActionType.HARD_BREAK -> {
                        if (isWrap(sourceActionLine.sourceActions, index + 1, lineLength + 1)) {
                            appendLine()
                            append(" ".repeat((sourceActionLine.indents + 1) * indentLength), sourceAction.location)
                            lineLength = (sourceActionLine.indents + 1) * indentLength
                        } else {
                            append(" ", sourceAction.location)
                        }
                    }
                    SourceActionType.ALIGN -> {}
                }
                index++
            }
            appendLine()
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

        private fun append(content: String, location: SourceLocation) {
            sourceBuilder.append(content)
            if (sourceBuilderLabeled != null) {
                sourceBuilderLabeled.append(content)
                val locationLabel = locationLabels.lastOrNull()
                if (locationLabel != null && locationLabel.matches(location)) {
                    locationLabel.size += content.length
                } else {
                    locationLabels.add(LocationLabel(content.length, location.line, location.column))
                }
            }
        }

        private fun appendLine() {
            sourceBuilder.appendLine()
            if (sourceBuilderLabeled != null) {
                sourceBuilderLabeled.appendLine()
                sourceBuilderLabeled.append("//")
                locationLabels.forEach { sourceBuilderLabeled.append(" $it") }
                sourceBuilderLabeled.appendLine()
                locationLabels.clear()
            }
        }

        private data class LocationLabel(var size: Int, val line: Int, val column: Int) {

            fun matches(location: SourceLocation): Boolean {
                return line == location.line && column == location.column
            }

            override fun toString(): String {
                return "$size:$line:$column"
            }
        }
    }
}
