/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.general

import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that serializes the report file.
 */
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
        if (projectContext.report.entryPoints.isNotEmpty()) {
            builder.appendLine("entryPoints:")
            projectContext.report.entryPoints.forEach {
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
