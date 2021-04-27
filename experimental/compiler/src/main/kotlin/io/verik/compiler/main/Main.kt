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

import io.verik.plugin.VerikPluginExtension
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention

object Main {

    fun run(project: Project, extension: VerikPluginExtension) {
        val projectContext = getProjectContext(project, extension)
        val kotlinCompiler = KotlinCompiler()
        kotlinCompiler.compile(projectContext)
    }

    private fun getProjectContext(project: Project, extension: VerikPluginExtension): ProjectContext {
        val messagePrinter = MessagePrinter(extension.verbose)
        val inputTextFiles = ArrayList<TextFile>()
        project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets.forEach { sourceSet ->
            sourceSet.allSource.forEach { file ->
                inputTextFiles.add(TextFile(file.toPath(), file.readText()))
            }
        }
        return ProjectContext(messagePrinter, inputTextFiles)
    }
}