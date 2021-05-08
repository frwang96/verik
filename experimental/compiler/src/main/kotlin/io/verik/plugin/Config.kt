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

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class Config(
    val timestamp: String,
    val projectName: String,
    val projectDir: Path,
    val buildDir: Path,
    val projectFiles: List<Path>,
    val top: String,
    val verbose: Boolean,
    val printStackTrace: Boolean,
    val labelLines: Boolean
) {

    companion object {

        operator fun invoke(project: Project, extension: VerikPluginExtension): Config {
            val top = extension.top
                ?: throw GradleException("Verik configuration failed: Elaboration top not specified")

            return Config(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
                project.name,
                project.projectDir.toPath(),
                getOutputDir(project),
                getInputFiles(project),
                top,
                extension.verbose,
                extension.printStackTrace,
                extension.labelLines
            )
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

        fun getOutputDir(project: Project): Path {
            return project.buildDir.resolve("verik").toPath()
        }
    }
}