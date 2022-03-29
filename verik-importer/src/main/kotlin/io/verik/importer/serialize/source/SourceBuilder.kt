/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.source

import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.serialize.general.FileHeaderBuilder
import java.nio.file.Path

/**
 * Builder for source text files that handles indentation.
 */
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
        "UNUSED_PARAMETER",
        "unused",
        "ClassName",
        "ConvertSecondaryConstructorToPrimary",
        "FunctionName",
        "LongLine",
        "PropertyName",
        "SpellCheckingInspection"
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
        appendLine("@file:Verik")
        appendLine("@file:Suppress(")
        indent {
            append("\"${SUPPRESSED_INSPECTIONS[0]}\"")
            SUPPRESSED_INSPECTIONS.drop(1).forEach {
                appendLine(",")
                append("\"$it\"")
            }
            appendLine()
        }
        appendLine(")")
        appendLine()
        appendLine("package $packageName")
        appendLine()
        appendLine("import io.verik.core.*")
    }
}
