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

package io.verik.compiler.serialize.general

import io.verik.compiler.main.Platform
import io.verik.compiler.main.VerikConfig
import java.nio.file.Path

object FileHeaderBuilder {

    fun build(config: VerikConfig, inputPath: Path?, outputPath: Path, headerStyle: HeaderStyle): String {
        val lines = ArrayList<String>()
        val inputPathString = inputPath?.let { Platform.getStringFromPath(it.toAbsolutePath()) }
        val outputPathString = Platform.getStringFromPath(outputPath.toAbsolutePath())

        lines.add("Toolchain : ${config.toolchain}")
        lines.add("Date      : ${config.timestamp}")
        lines.add("Project   : ${config.projectName}")
        if (inputPathString != null)
            lines.add("Input     : $inputPathString")
        lines.add("Output    : $outputPathString")

        val builder = StringBuilder()
        when (headerStyle) {
            HeaderStyle.SYSTEM_VERILOG -> {
                lines.forEach { builder.appendLine("// $it") }
                builder.appendLine()
                builder.appendLine("`ifndef VERIK")
                builder.appendLine("`define VERIK")
                builder.appendLine("`timescale ${config.timescale}")
                builder.appendLine("`endif")
            }
            HeaderStyle.TEXT -> {
                lines.forEach { builder.appendLine("# $it") }
            }
        }
        builder.appendLine()
        return builder.toString()
    }

    enum class HeaderStyle { SYSTEM_VERILOG, TEXT }
}
