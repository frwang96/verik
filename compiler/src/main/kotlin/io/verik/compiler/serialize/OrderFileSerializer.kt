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

import io.verik.compiler.ast.property.SourceType
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import java.nio.file.Path

object OrderFileSerializer {

    fun serialize(projectContext: ProjectContext, packageTextFiles: List<TextFile>): TextFile {
        val inputPath = projectContext.config.projectDir.resolve("src")
        val outputPath = projectContext.config.buildDir.resolve("order.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            inputPath,
            outputPath,
            FileHeaderBuilder.HeaderStyle.YAML
        )
        val paths = getPaths(projectContext, packageTextFiles)

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("top: ${projectContext.config.top}")
        builder.appendLine("order:")
        paths.forEach {
            builder.appendLine("  - ${projectContext.config.buildDir.relativize(it)}")
        }

        return TextFile(outputPath, builder.toString())
    }

    private fun getPaths(projectContext: ProjectContext, packageTextFiles: List<TextFile>): List<Path> {
        val paths = ArrayList<Path>()
        packageTextFiles.forEach {
            paths.add(it.path)
        }
        projectContext.project.files().forEach {
            if (it.sourceType == SourceType.COMPONENT)
                paths.add(it.getOutputPathNotNull())
        }
        return paths
    }
}