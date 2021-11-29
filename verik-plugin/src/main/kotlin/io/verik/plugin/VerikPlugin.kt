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

import io.verik.compiler.main.VerikException
import io.verik.compiler.main.VerikMain
import io.verik.importer.main.VerikImporterException
import io.verik.importer.main.VerikImporterMain
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginConvention
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import io.verik.compiler.message.GradleMessagePrinter as VerikGradleMessagePrinter
import io.verik.importer.message.GradleMessagePrinter as VerikImporterGradleMessagePrinter

@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(KotlinPluginWrapper::class.java)
        val verikTask = createVerikTask(project)
        val verikImportTask = createVerikImportTask(project)
        verikTask.dependsOn(verikImportTask)

        val sourceSets = project.convention.getPlugin(JavaPluginConvention::class.java).sourceSets
        sourceSets.getByName("main").java {
            it.srcDir(VerikImporterConfigBuilder.getBuildDir(project).resolve("src"))
        }
    }

    private fun createVerikTask(project: Project): Task {
        val extension = project.extensions.create("verik", VerikPluginExtension::class.java)
        val task = project.tasks.create("verik") {
            it.doLast {
                try {
                    VerikMain.run(VerikConfigBuilder.getConfig(project, extension))
                } catch (exception: Exception) {
                    if (exception !is VerikException) {
                        printErrorMessage(
                            exception,
                            extension.debug,
                            VerikGradleMessagePrinter.Companion::printStackTrace
                        )
                    }
                    throw GradleException("Verik compilation failed")
                }
            }
        }

        task.group = "verik"
        task.inputs.property("timescale", { extension.timescale })
        task.inputs.property("version", { VerikConfigUtil.getVersion(project) })
        task.inputs.property("debug", { extension.debug })
        task.inputs.property("suppressedWarnings", { extension.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.promotedWarnings })
        task.inputs.property("maxErrorCount", { extension.maxErrorCount })
        task.inputs.property("labelSourceLocations", { extension.labelSourceLocations })
        task.inputs.property("wrapLength", { extension.wrapLength })
        task.inputs.property("indentLength", { extension.indentLength })
        task.inputs.property("enableDeadCodeElimination", { extension.enableDeadCodeElimination })
        task.inputs.files({ VerikConfigBuilder.getSourceSetConfigs(project).flatMap { it.files } })
        task.outputs.dir(VerikConfigBuilder.getBuildDir(project))
        return task
    }

    private fun createVerikImportTask(project: Project): Task {
        val extension = project.extensions.create("verikImport", VerikImporterPluginExtension::class.java)
        val task = project.tasks.create("verikImport") {
            it.doLast {
                try {
                    VerikImporterMain.run(VerikImporterConfigBuilder.getConfig(project, extension))
                } catch (exception: Exception) {
                    if (exception !is VerikImporterException) {
                        printErrorMessage(
                            exception,
                            extension.debug,
                            VerikImporterGradleMessagePrinter::printStackTrace
                        )
                    }
                    throw GradleException("Verik import failed")
                }
            }
        }
        task.group = "verik"
        task.inputs.property("debug", { extension.debug })
        task.inputs.property("suppressedWarnings", { extension.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.promotedWarnings })
        task.inputs.property("labelSourceLocations", { extension.labelSourceLocations })
        task.inputs.files({ extension.importedFiles })
        task.outputs.dirs({
            if (extension.importedFiles.isNotEmpty()) {
                VerikImporterConfigBuilder.getBuildDir(project)
            } else listOf()
        })
        return task
    }

    private fun printErrorMessage(
        exception: Exception,
        debug: Boolean,
        stackTracePrinter: (Array<StackTraceElement>) -> Unit
    ) {
        print("e: ")
        if (debug) {
            print("Internal error")
            if (exception.message != null) print(": ${exception.message}")
            println()
            stackTracePrinter(exception.stackTrace)
        } else {
            println("Internal error: Set debug mode for more details")
        }
    }
}
