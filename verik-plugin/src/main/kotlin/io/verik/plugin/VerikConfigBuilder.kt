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
import org.gradle.api.Project
import org.gradle.api.internal.artifacts.dependencies.DefaultProjectDependency
import org.gradle.api.plugins.JavaPluginExtension
import java.nio.file.Path

object VerikConfigBuilder {

    fun getConfig(project: Project, extension: VerikPluginExtension): VerikConfig {
        return VerikConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            buildDir = getBuildDir(project),
            sourceSetConfigs = getSourceSetConfigs(project),
            timescale = extension.timescale,
            enableDeadCodeElimination = extension.enableDeadCodeElimination,
            labelSourceLocations = extension.labelSourceLocations,
            indentLength = extension.indentLength,
            wrapLength = extension.wrapLength,
            suppressedWarnings = extension.suppressedWarnings,
            promotedWarnings = extension.promotedWarnings,
            maxErrorCount = extension.maxErrorCount,
            debug = extension.debug
        )
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik").toPath()
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
            .filter { it.toString().endsWith(".kt") }
            .sorted()
        return SourceSetConfig(project.name, filesSorted)
    }
}
