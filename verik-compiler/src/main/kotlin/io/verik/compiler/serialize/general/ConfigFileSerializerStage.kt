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

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ConfigFileSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("config.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            outputPath,
            FileHeaderBuilder.HeaderStyle.TEXT
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("timescale: ${projectContext.config.timescale}")
        if (projectContext.config.entryPoints.isNotEmpty()) {
            builder.appendLine("entryPoints:")
            projectContext.config.entryPoints.forEach {
                builder.appendLine("  - $it")
            }
        }
        builder.appendLine("enableDeadCodeElimination: ${projectContext.config.enableDeadCodeElimination}")
        projectContext.outputContext.configTextFile = TextFile(outputPath, builder.toString())
    }
}
