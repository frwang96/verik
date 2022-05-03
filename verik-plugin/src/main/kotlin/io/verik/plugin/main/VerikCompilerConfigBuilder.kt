/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.compiler.main.SourceSetConfig
import io.verik.compiler.main.VerikCompilerConfig
import io.verik.plugin.`object`.VerikDomainObjectImpl
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.plugins.JavaPluginExtension
import java.nio.file.Path
import kotlin.io.path.extension

/**
 * Factory class that builds a [VerikCompilerConfig] object.
 */
object VerikCompilerConfigBuilder {

    fun getConfig(project: Project, extension: VerikDomainObjectImpl): VerikCompilerConfig {
        return VerikCompilerConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            buildDir = getBuildDir(project),
            sourceSetConfigs = getSourceSetConfigs(project),
            timescale = extension.compile.timescale,
            entryPoints = extension.compile.entryPoints,
            enableDeadCodeElimination = extension.compile.enableDeadCodeElimination,
            labelLines = extension.compile.labelLines,
            indentLength = extension.compile.indentLength,
            wrapLength = extension.compile.wrapLength,
            suppressedWarnings = extension.compile.suppressedWarnings,
            promotedWarnings = extension.compile.promotedWarnings,
            maxErrorCount = extension.maxErrorCount,
            debug = extension.debug
        )
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik-compile").toPath()
    }

    fun getSourceSetConfigs(project: Project): List<SourceSetConfig> {
        val sourceSetConfigs = ArrayList<SourceSetConfig>()
        sourceSetConfigs.add(getSourceSetConfig(project))
        project.configurations.all { configuration ->
            configuration.dependencies.forEach {
                if (it is DefaultProjectDependency) {
                    sourceSetConfigs.add(getSourceSetConfig(it.dependencyProject))
                }
            }
        }
        sourceSetConfigs.sortBy { it.name }
        return sourceSetConfigs
    }

    private fun getSourceSetConfig(project: Project): SourceSetConfig {
        val files = ArrayList<Path>()
        project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.forEach { sourceSet ->
            sourceSet.allSource.forEach { file ->
                files.add(file.toPath())
            }
        }
        val filesSorted = files
            .filter { it.extension == "kt" }
            .sorted()
        return SourceSetConfig(project.name, filesSorted)
    }
}
