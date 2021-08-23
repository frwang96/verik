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

import io.verik.compiler.main.Config
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConfigBuilder {

    fun getConfig(project: Project, extension: VerikPluginExtension): Config {
        val version = getVersion(project)
            ?: throw GradleException("Verik configuration failed: Could not determine version number")
        val top = extension.top
            ?: throw GradleException("Verik configuration failed: Elaboration top not specified")

        return Config(
            version,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
            project.name,
            project.projectDir.toPath(),
            getBuildDir(project),
            getInputFiles(project),
            top,
            extension.verbose,
            extension.debug,
            suppressCompileWarnings = false,
            extension.labelLines,
            extension.wrapLength,
            extension.indentLength
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

    fun getInputFiles(project: Project): List<Path> {
        val inputFiles = ArrayList<Path>()
        project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.forEach { sourceSet ->
            sourceSet.allSource.forEach { file ->
                inputFiles.add(file.toPath())
            }
        }
        return inputFiles
            .filter { it.toString().endsWith(".kt") }
            .sorted()
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik").toPath()
    }
}
