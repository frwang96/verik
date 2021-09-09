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
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.message.Messages
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
    private val labelLineStack = ArrayDeque<Int>()
    private var indent = 0

    fun toTextFile(): TextFile {
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Serialized source must end with a new line")

        val sourceBuilder = StringBuilder()
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            file.inputPath,
            file.getOutputPathNotNull(),
            FileHeaderBuilder.HeaderStyle.SV
        )
        sourceBuilder.append(fileHeader)

        val labelLength = sourceActionLines
            .flatMap { it.sourceActions }
            .maxOfOrNull { it.line.toString().length }
            ?: 0
        val sourceActionBuilder = SourceActionBuilder(
            sourceBuilder,
            projectContext.config.labelLines,
            projectContext.config.wrapLength,
            projectContext.config.indentLength,
            labelLength
        )
        sourceActionBuilder.build(sourceActionLines)
        return TextFile(file.getOutputPathNotNull(), sourceBuilder.toString())
    }

    fun label(element: EElement, block: () -> Unit) {
        labelLineStack.push(element.location.line)
        block()
        labelLineStack.pop()
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
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.REGULAR, content, labelLineStack.peek()!!))
    }

    fun softBreak() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.SOFT_BREAK, "", labelLineStack.peek()!!))
    }

    fun hardBreak() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.HARD_BREAK, "", labelLineStack.peek()!!))
    }

    fun align() {
        sourceActionLine.sourceActions.add(SourceAction(SourceActionType.ALIGN, "", labelLineStack.peek()!!))
    }

    enum class SourceActionType { REGULAR, SOFT_BREAK, HARD_BREAK, ALIGN }

    data class SourceAction(val type: SourceActionType, val content: String, val line: Int)

    data class SourceActionLine(val indents: Int, val sourceActions: ArrayList<SourceAction>)

    class SourceActionBuilder(
        private val sourceBuilder: StringBuilder,
        private val labelLines: Boolean,
        private val wrapLength: Int,
        private val indentLength: Int,
        private val labelLength: Int
    ) {

        private val labelIndentLength = ((labelLength + 4 + indentLength) / indentLength) * indentLength

        fun build(sourceActionLines: ArrayList<SourceActionLine>) {
            val alignments = ArrayList(sourceActionLines.map { getAlignment(it) })
            matchAlignment(alignments)
            sourceActionLines.zip(alignments).forEach { (sourceActionLine, alignment) ->
                buildLine(sourceActionLine, alignment)
            }
        }

        private fun getAlignment(sourceActionLine: SourceActionLine): Int? {
            var alignment = sourceActionLine.indents * indentLength
            if (labelLines)
                alignment += labelIndentLength
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
            var lineLength = labelLine(sourceActionLine.sourceActions, index)
            if (sourceActionLine.sourceActions.isEmpty()) {
                sourceBuilder.appendLine()
                return
            }
            sourceBuilder.append(" ".repeat(sourceActionLine.indents * indentLength))
            lineLength += sourceActionLine.indents * indentLength

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
                            lineLength = labelLine(sourceActionLine.sourceActions, index + 1)
                            sourceBuilder.append(" ".repeat((sourceActionLine.indents + 1) * indentLength))
                            lineLength += (sourceActionLine.indents + 1) * indentLength
                        }
                    }
                    SourceActionType.HARD_BREAK -> {
                        if (isWrap(sourceActionLine.sourceActions, index + 1, lineLength + 1)) {
                            sourceBuilder.appendLine()
                            lineLength = labelLine(sourceActionLine.sourceActions, index + 1)
                            sourceBuilder.append(" ".repeat((sourceActionLine.indents + 1) * indentLength))
                            lineLength += (sourceActionLine.indents + 1) * indentLength
                        } else {
                            sourceBuilder.append(" ")
                        }
                    }
                    SourceActionType.ALIGN -> {}
                }
                index++
            }
            sourceBuilder.appendLine()
        }

        private fun labelLine(sourceActions: ArrayList<SourceAction>, offset: Int): Int {
            return if (labelLines) {
                val line = sourceActions.getOrNull(offset)?.line
                val label = if (line != null) "`_(${line.toString().padStart(labelLength, ' ')})"
                else "`_(${" ".repeat(labelLength)})"
                sourceBuilder.append(label.padEnd(labelIndentLength, ' '))
                labelIndentLength
            } else 0
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
}
