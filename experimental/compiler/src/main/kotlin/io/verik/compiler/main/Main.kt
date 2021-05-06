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

package io.verik.compiler.main

import io.verik.compiler.cast.ProjectCaster
import io.verik.compiler.serialize.ProjectSerializer
import io.verik.plugin.VerikPluginExtension
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

lateinit var messageCollector: MessageCollector

object Main {

    fun run(project: Project, extension: VerikPluginExtension) {
        val config = getConfig(project, extension)
        messageCollector = GradleMessageCollector(config)
        val projectContext = ProjectContext(config)

        readFiles(projectContext)
        KotlinCompiler().compile(projectContext)
        ProjectCaster.cast(projectContext)
        ProjectSerializer.serialize(projectContext)

        if (messageCollector.errorCount != 0) throw GradleException("Verik compilation failed")
    }

    private fun getConfig(project: Project, extension: VerikPluginExtension): Config {
        val projectFiles = ArrayList<Path>()
        project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.forEach { sourceSet ->
            sourceSet.allSource.forEach { file ->
                projectFiles.add(file.toPath())
            }
        }

        return Config(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")),
            project.name,
            project.projectDir.toPath(),
            project.buildDir.resolve("verik").toPath(),
            projectFiles,
            extension.verbose,
            extension.printStackTrace,
            extension.labelLines
        )
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.inputTextFiles = projectContext.config.projectFiles.map {
            TextFile(it, Files.readString(it))
        }
    }
}