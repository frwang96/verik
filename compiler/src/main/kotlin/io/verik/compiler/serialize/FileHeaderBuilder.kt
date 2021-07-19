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

import io.verik.compiler.main.ProjectContext
import java.nio.file.Path

object FileHeaderBuilder {

    fun build(projectContext: ProjectContext, inputPath: Path, outputPath: Path, headerStyle: HeaderStyle): String {
        val lines = ArrayList<String>()

        lines.add("Project: ${projectContext.config.projectName}")
        lines.add("Input:   ${projectContext.config.projectDir.relativize(inputPath)}")
        lines.add("Output:  ${projectContext.config.projectDir.relativize(outputPath)}")
        lines.add("Date:    ${projectContext.config.timestamp}")
        lines.add("Version: ${projectContext.config.version}")

        val builder = StringBuilder()
        when (headerStyle) {
            HeaderStyle.SV -> {
                builder.appendLine("/*")
                lines.forEach { builder.appendLine(" * $it") }
                builder.appendLine(" */")
                builder.appendLine()
                builder.appendLine("`ifndef VERIK")
                builder.appendLine("`define VERIK")
                if (projectContext.config.labelLines)
                    builder.appendLine("`define _(N)")
                builder.appendLine("`timescale ${projectContext.config.timescale}")
                builder.appendLine("`endif")
            }
            HeaderStyle.YAML -> {
                lines.forEach { builder.appendLine("# $it") }
            }
        }
        builder.appendLine()
        return builder.toString()
    }

    enum class HeaderStyle { SV, YAML }
}