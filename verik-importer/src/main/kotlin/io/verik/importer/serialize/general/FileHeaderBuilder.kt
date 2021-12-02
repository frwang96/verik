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

package io.verik.importer.serialize.general

import io.verik.importer.main.ImporterContext
import io.verik.importer.main.Platform
import java.nio.file.Path

object FileHeaderBuilder {

    fun build(importerContext: ImporterContext, outputPath: Path, headerStyle: HeaderStyle): String {
        val lines = ArrayList<String>()
        val outputPathString = Platform.getStringFromPath(outputPath.toAbsolutePath())

        lines.add("Project: ${importerContext.config.projectName}")
        lines.add("Output:  $outputPathString")
        lines.add("Date:    ${importerContext.config.timestamp}")
        lines.add("Version: verik:${importerContext.config.version}")

        val builder = StringBuilder()
        when (headerStyle) {
            HeaderStyle.KOTLIN -> {
                builder.appendLine("/*")
                lines.forEach { builder.appendLine(" * $it") }
                builder.appendLine(" */")
            }
            HeaderStyle.YAML -> {
                lines.forEach { builder.appendLine("# $it") }
            }
        }
        builder.appendLine()
        return builder.toString()
    }

    enum class HeaderStyle { KOTLIN, YAML }
}