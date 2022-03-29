/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.general

import io.verik.compiler.main.Platform
import io.verik.compiler.main.VerikConfig
import java.nio.file.Path

/**
 * Utility class that builds file headers for output text files.
 */
object FileHeaderBuilder {

    fun build(config: VerikConfig, inputPath: Path?, outputPath: Path, commentStyle: CommentStyle): String {
        val lines = ArrayList<String>()
        val inputPathString = inputPath?.let { Platform.getStringFromPath(it.toAbsolutePath()) }
        val outputPathString = Platform.getStringFromPath(outputPath.toAbsolutePath())

        lines.add("Toolchain : ${config.toolchain}")
        lines.add("Date      : ${config.timestamp}")
        lines.add("Project   : ${config.projectName}")
        if (inputPathString != null) {
            lines.add("Input     : $inputPathString")
        }
        lines.add("Output    : $outputPathString")

        val builder = StringBuilder()
        when (commentStyle) {
            CommentStyle.SLASH -> lines.forEach { builder.appendLine("// $it") }
            CommentStyle.HASH -> lines.forEach { builder.appendLine("# $it") }
        }
        return builder.toString()
    }

    enum class CommentStyle { SLASH, HASH }
}
