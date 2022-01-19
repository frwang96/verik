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

package io.verik.importer.serialize.source

import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.serialize.general.FileHeaderBuilder
import java.nio.file.Path

class SourceBuilder(
    projectContext: ProjectContext,
    private val packageName: String,
    private val outputPath: Path
) {

    private val lineBuilder = StringBuilder()
    private val sourceBuilder = StringBuilder()
    private var indentation = 0

    private val INDENT_LENGTH = 4
    private val SUPPRESSED_INSPECTIONS = listOf(
        "unused",
        "ClassName",
        "LongLine",
        "PropertyName"
    )

    init {
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            outputPath,
            FileHeaderBuilder.HeaderStyle.KOTLIN
        )
        sourceBuilder.append(fileHeader)
        buildHeader()
    }

    fun getTextFile(): TextFile {
        return TextFile(outputPath, sourceBuilder.toString())
    }

    fun indent(block: () -> Unit) {
        indentation++
        block()
        indentation--
    }

    fun append(content: String) {
        lineBuilder.append(content)
    }

    fun appendLine(content: String) {
        lineBuilder.append(content)
        appendLine()
    }

    fun appendLine() {
        if (lineBuilder.isEmpty()) {
            sourceBuilder.appendLine()
        } else {
            sourceBuilder.append(" ".repeat(INDENT_LENGTH * indentation))
            sourceBuilder.appendLine(lineBuilder)
            lineBuilder.clear()
        }
    }

    private fun buildHeader() {
        val suppressedInspectionsString = SUPPRESSED_INSPECTIONS.joinToString { "\"$it\"" }
        sourceBuilder.appendLine("@file:Verik")
        sourceBuilder.appendLine("@file:Suppress($suppressedInspectionsString)")
        sourceBuilder.appendLine()
        sourceBuilder.appendLine("package $packageName")
        sourceBuilder.appendLine()
        sourceBuilder.appendLine("import io.verik.core.*")
    }
}
