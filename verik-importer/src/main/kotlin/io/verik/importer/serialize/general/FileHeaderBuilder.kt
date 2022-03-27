/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.serialize.general

import io.verik.importer.main.Platform
import io.verik.importer.main.VerikImporterConfig
import java.nio.file.Path

object FileHeaderBuilder {

    fun build(config: VerikImporterConfig, outputPath: Path, headerStyle: HeaderStyle): String {
        val lines = ArrayList<String>()
        val outputPathString = Platform.getStringFromPath(outputPath.toAbsolutePath())

        lines.add("Toolchain : ${config.toolchain}")
        lines.add("Date      : ${config.timestamp}")
        lines.add("Project   : ${config.projectName}")
        lines.add("Output    : $outputPathString")

        val builder = StringBuilder()
        when (headerStyle) {
            HeaderStyle.KOTLIN -> {
                lines.forEach { builder.appendLine("// $it") }
            }
            HeaderStyle.TEXT -> {
                lines.forEach { builder.appendLine("# $it") }
            }
        }
        builder.appendLine()
        return builder.toString()
    }

    enum class HeaderStyle { KOTLIN, TEXT }
}
