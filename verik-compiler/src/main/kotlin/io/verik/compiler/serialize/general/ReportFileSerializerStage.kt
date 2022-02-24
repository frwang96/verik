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
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ReportFileSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("report.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            null,
            outputPath,
            FileHeaderBuilder.CommentStyle.HASH
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine()
        if (projectContext.config.sourceSetConfigs.any { it.files.isNotEmpty() }) {
            builder.appendLine("sourceSets:")
            projectContext.config.sourceSetConfigs.forEach { sourceSetConfig ->
                if (sourceSetConfig.files.isNotEmpty()) {
                    builder.appendLine("  ${sourceSetConfig.name}:")
                    sourceSetConfig.files.forEach {
                        builder.appendLine("    - ${Platform.getStringFromPath(it.toAbsolutePath())}")
                    }
                }
            }
        }
        builder.appendLine("enableDeadCodeElimination: ${projectContext.config.enableDeadCodeElimination}")
        if (projectContext.config.entryPoints.isNotEmpty()) {
            builder.appendLine("entryPoints:")
            projectContext.config.entryPoints.forEach {
                builder.appendLine("  - $it")
            }
        }
        if (projectContext.report.counts.isNotEmpty()) {
            builder.appendLine("counts:")
            projectContext.report.counts.forEach { (name, count) ->
                builder.appendLine("  $name: $count")
            }
        }

        projectContext.outputContext.reportTextFile = TextFile(outputPath, builder.toString())
    }
}
