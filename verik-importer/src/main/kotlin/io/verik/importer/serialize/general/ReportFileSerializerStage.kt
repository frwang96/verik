/*
 * SPDX-License-Identifier: Apache-2.0
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
