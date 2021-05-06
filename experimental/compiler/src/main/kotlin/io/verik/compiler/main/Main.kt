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
import io.verik.plugin.Config
import org.gradle.api.GradleException
import java.nio.file.Files

lateinit var messageCollector: MessageCollector

object Main {

    fun run(config: Config) {
        messageCollector = GradleMessageCollector(config)
        val projectContext = ProjectContext(config)

        messageCollector.info("Configuration: Project name: ${config.projectName}")
        messageCollector.info("Configuration: Top: ${config.top}")

        readFiles(projectContext)
        KotlinCompiler().compile(projectContext)
        ProjectCaster.cast(projectContext)
        ProjectSerializer.serialize(projectContext)

        if (messageCollector.errorCount != 0) throw GradleException("Verik compilation failed")
        writeFiles(projectContext)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.inputTextFiles = projectContext.config.projectFiles.map {
            messageCollector.info("Read file: ${projectContext.config.projectDir.relativize(it)}")
            TextFile(it, Files.readString(it))
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        if (Files.exists(projectContext.config.buildDir)) {
            Files.walk(projectContext.config.buildDir)
                .sorted(Comparator.reverseOrder())
                .forEach { Files.delete(it) }
        }
        val outputTextFiles = projectContext.outputTextFiles.sortedBy { it.path }
        outputTextFiles.forEach {
            messageCollector.info("Write file: ${projectContext.config.projectDir.relativize(it.path)}")
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }
}