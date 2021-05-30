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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.main.MessageLocation
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.main.messageCollector
import java.nio.file.Path

class SourceBuilder(
    projectContext: ProjectContext,
    inputPath: Path,
    outputPath: Path
): TextFileBuilder(projectContext, inputPath, outputPath) {

    private val sourceActions = ArrayList<SourceAction>()

    init {
        if (projectContext.config.labelLines) {
            sourceBuilder.appendLine("`define _(N)")
            sourceBuilder.appendLine("")
        }
    }

    override fun toTextFile(): TextFile {
        sourceActions.forEach {
            if (it.location != null && it.location.path != inputPath)
                messageCollector.error("Mismatch in file path for source location", it.location)
        }

        val labelLength = sourceActions.maxOfOrNull {
            if (it.location != null) it.location.line.toString().length
            else 0
        } ?: 0
        val sourceActionBuilder = SourceActionBuilder(
            sourceBuilder,
            projectContext.config.labelLines,
            projectContext.config.indentLength,
            labelLength,
            projectContext.config.wrapLength
        )
        sourceActions.forEach { sourceActionBuilder.build(it) }

        return super.toTextFile()
    }

    fun appendLine(content: String, element: VkElement?) {
        append(content, element)
        appendLine()
    }

    fun appendLine() {
        sourceActions.add(SourceAction(SourceActionType.NEW_LINE, "", null))
    }

    fun indent(block: () -> Unit) {
        sourceActions.add(SourceAction(SourceActionType.INDENT_IN, "", null))
        block()
        sourceActions.add(SourceAction(SourceActionType.INDENT_OUT, "", null))
    }

    private fun append(content: String, element: VkElement?) {
        sourceActions.add(SourceAction(SourceActionType.REGULAR, content, element?.location))
    }

    enum class SourceActionType { REGULAR, NEW_LINE, INDENT_IN, INDENT_OUT, SOFT_BREAK, HARD_BREAK }

    data class SourceAction(val type: SourceActionType, val content: String, val location: MessageLocation?)

    class SourceActionBuilder(
        private val sourceBuilder: StringBuilder,
        private val labelLines: Boolean,
        private val indentLength: Int,
        private val labelLength: Int,
        wrapLength: Int
    ) {

        private val maxLineLength = wrapLength - (if (labelLines) labelLength + 5 else 0)
        private val lineBuilder = StringBuilder()
        private val tokenBuilder = StringBuilder()
        private var isHardBreak = false
        private var line: Int? = null
        private var indent = 0

        fun build(sourceAction: SourceAction) {
            when (sourceAction.type) {
                SourceActionType.REGULAR -> {
                    if (lineBuilder.isEmpty() && tokenBuilder.isEmpty()) {
                        tokenBuilder.append(" ".repeat(indent * indentLength))
                    }
                    line = line ?: sourceAction.location?.line
                    tokenBuilder.append(sourceAction.content)
                }
                SourceActionType.NEW_LINE -> {
                    flushTokenToLine()
                    flushLineToSource()
                    isHardBreak = false
                }
                SourceActionType.INDENT_IN -> indent++
                SourceActionType.INDENT_OUT -> indent--
                SourceActionType.SOFT_BREAK -> {
                    flushTokenToLine()
                    isHardBreak = false
                }
                SourceActionType.HARD_BREAK -> {
                    flushTokenToLine()
                    isHardBreak = true
                }
            }
        }

        private fun flushTokenToLine() {
            val lineLength = lineBuilder.length + tokenBuilder.length + (if (isHardBreak) 1 else 0)
            if (lineBuilder.isNotEmpty() && lineLength > maxLineLength) {
                flushLineToSource()
                lineBuilder.append(" ".repeat((indent + 1) * indentLength))
            } else {
                if (isHardBreak) lineBuilder.append(" ")
            }
            lineBuilder.append(tokenBuilder)
            tokenBuilder.clear()
        }

        private fun flushLineToSource() {
            if (labelLines) {
                val label = if (line != null) "`_(${line.toString().padStart(labelLength, ' ')}) "
                else "`_(${" ".repeat(labelLength)}) "
                sourceBuilder.append(label)
            }
            if (lineBuilder.isNotEmpty()) sourceBuilder.append("$lineBuilder")
            sourceBuilder.appendLine()
            lineBuilder.clear()
            line = null
        }
    }
}