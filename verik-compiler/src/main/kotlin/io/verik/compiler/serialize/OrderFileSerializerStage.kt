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

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import java.nio.file.Path

object OrderFileSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val inputPath = projectContext.config.inputSourceDir
        val outputPath = projectContext.config.buildDir.resolve("order.txt")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            inputPath,
            outputPath,
            FileHeaderBuilder.HeaderStyle.TXT
        )
        val paths = getPaths(projectContext)

        val builder = StringBuilder()
        builder.append(fileHeader)
        paths.forEach {
            builder.appendLine(projectContext.config.buildDir.relativize(it))
        }
        projectContext.outputTextFiles.add(TextFile(outputPath, builder.toString()))
    }

    private fun getPaths(projectContext: ProjectContext): List<Path> {
        val paths = ArrayList<Path>()
        projectContext.project.basicPackages.forEach {
            if (!it.isEmpty())
                paths.add(it.outputPath.resolve("Pkg.sv"))
        }
        projectContext.project.rootPackage.files.forEach {
            if (it.members.isNotEmpty())
                paths.add(it.getOutputPathNotNull())
        }
        return paths
    }
}
