/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.target

import io.verik.compiler.main.Platform
import io.verik.plugin.config.TargetConfig
import java.nio.file.Path

/**
 * Header builder for files that are generated for a target.
 */
object TargetFileHeaderBuilder {

    fun build(targetConfig: TargetConfig, path: Path): String {
        val lines = ArrayList<String>()
        val pathString = Platform.getStringFromPath(path.toAbsolutePath())

        lines.add("Toolchain : ${targetConfig.projectConfig.toolchain}")
        lines.add("Date      : ${targetConfig.projectConfig.timestamp}")
        lines.add("Project   : ${targetConfig.projectConfig.name}")
        lines.add("Target    : ${targetConfig.name}")
        lines.add("Output    : $pathString")

        val builder = StringBuilder()
        lines.forEach { builder.appendLine("# $it") }
        return builder.toString()
    }
}
