/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.target

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.serialize.general.FileHeaderBuilder
import java.nio.file.Path

class TargetSourceBuilder(
    projectContext: ProjectContext,
    private val path: Path
) {

    private val sourceBuilder = StringBuilder()
    private val indentLength = projectContext.config.indentLength
    private var indent = 0

    init {
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            path,
            FileHeaderBuilder.CommentStyle.SLASH
        )
        sourceBuilder.append(fileHeader)
        sourceBuilder.appendLine()
    }

    fun appendLine() {
        sourceBuilder.appendLine()
    }

    fun appendLine(content: String) {
        content.lines().forEach {
            val line = it.trimEnd()
            if (line != "") {
                val trimmedLine = line.trimStart()
                val totalIndent = indent + (line.length - trimmedLine.length) / 4
                sourceBuilder.append(" ".repeat(totalIndent * indentLength))
                sourceBuilder.appendLine(trimmedLine)
            } else {
                sourceBuilder.appendLine()
            }
        }
    }

    fun indent(block: () -> Unit) {
        indent++
        block()
        indent--
    }

    fun toTextFile(): TextFile {
        return TextFile(path, sourceBuilder.toString())
    }
}
