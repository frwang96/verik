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

package io.verik.plugin

import io.verik.compiler.main.SourceSetConfig
import io.verik.compiler.main.VerikConfig
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.plugins.JavaPluginConvention
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConfigBuilder {

    fun getConfig(project: Project, extension: VerikPluginExtension): VerikConfig {
        val version = getVersion(project)
            ?: throw GradleException("Verik configuration failed: Could not determine version number")

        return VerikConfig(
            version = version,
            timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
            projectName = project.name,
            projectDir = project.projectDir.toPath(),
            buildDir = getBuildDir(project),
            sourceSetConfigs = getSourceSetConfigs(project),
            timescale = extension.timescale,
            debug = extension.debug,
            suppressedWarnings = extension.suppressedWarnings,
            promotedWarnings = extension.promotedWarnings,
            maxErrorCount = extension.maxErrorCount,
            labelLines = extension.labelLines,
            wrapLength = extension.wrapLength,
            indentLength = extension.indentLength,
            enableDeadCodeElimination = extension.enableDeadCodeElimination
        )
    }

    fun getVersion(project: Project): String? {
        val configuration = project.configurations.getByName("compileClasspath")
        configuration.allDependencies.forEach {
            if (it.group == "io.verik" && it.name == "verik-core")
                return it.version.toString()
        }
        return null
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

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik").toPath()
    }

    private fun getSourceSetConfig(project: Project): SourceSetConfig {
        val files = ArrayList<Path>()
        project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.forEach { sourceSet ->
            sourceSet.allSource.forEach { file ->
                files.add(file.toPath())
            }
        }
        val filesSorted = files
            .filter { it.toString().endsWith(".kt") }
            .sorted()
        return SourceSetConfig(project.name, filesSorted)
    }
}
