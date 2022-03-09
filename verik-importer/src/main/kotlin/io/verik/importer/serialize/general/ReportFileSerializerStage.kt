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

import io.verik.importer.common.TextFile
import io.verik.importer.main.Platform
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object ReportFileSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("report.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext.config,
            outputPath,
            FileHeaderBuilder.HeaderStyle.TEXT
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        if (projectContext.config.importedFiles.isNotEmpty()) {
            builder.appendLine("importedFiles:")
            projectContext.config.importedFiles.forEach {
                builder.appendLine("  - ${Platform.getStringFromPath(it.toAbsolutePath())}")
            }
        }
        if (projectContext.includedTextFiles.keys.isNotEmpty()) {
            builder.appendLine("includedFiles:")
            projectContext.includedTextFiles.keys.forEach {
                builder.appendLine("  - ${Platform.getStringFromPath(it.toAbsolutePath())}")
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