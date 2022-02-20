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
