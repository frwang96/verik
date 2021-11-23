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

import io.verik.compiler.main.VerikMain
import io.verik.compiler.message.GradleMessagePrinter
import io.verik.compiler.message.MessageCollectorException
import io.verik.import.main.VerikImportMain
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(KotlinPluginWrapper::class.java)
        createVerikTask(project)
        createVerikImportTask(project)
    }

    private fun createVerikTask(project: Project) {
        val extension = project.extensions.create("verik", VerikPluginExtension::class.java)
        val task = project.tasks.create("verik") {
            it.doLast {
                try {
                    VerikMain.run(VerikConfigBuilder.getConfig(project, extension))
                } catch (exception: Exception) {
                    if (exception !is MessageCollectorException) {
                        print("e: ")
                        if (extension.debug) {
                            print("INTERNAL_ERROR: ${exception::class.simpleName}")
                            if (exception.message != null) print(": ${exception.message}")
                            println()
                            GradleMessagePrinter.printStackTrace(exception.stackTrace)
                        } else {
                            println("Internal error: Set debug mode for more details")
                        }
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
        task.inputs.property("labelLines", { extension.labelLines })
        task.inputs.property("wrapLength", { extension.wrapLength })
        task.inputs.property("indentLength", { extension.indentLength })
        task.inputs.property("enableDeadCodeElimination", { extension.enableDeadCodeElimination })
        task.inputs.files({ VerikConfigBuilder.getSourceSetConfigs(project).flatMap { it.files } })
        task.outputs.dir(VerikConfigBuilder.getBuildDir(project))
    }

    private fun createVerikImportTask(project: Project) {
        val extension = project.extensions.create("verikImport", VerikImportPluginExtension::class.java)
        val task = project.tasks.create("verikImport") {
            it.doLast {
                try {
                    VerikImportMain.run(VerikImportConfigBuilder.getConfig(project, extension))
                } catch (exception: Exception) {
                    print("e: ")
                    if (extension.debug) {
                        print("INTERNAL_ERROR: ${exception::class.simpleName}")
                        if (exception.message != null) print(": ${exception.message}")
                        println()
                    } else {
                        println("Internal error: Set debug mode for more details")
                    }
                    throw GradleException("Verik import failed")
                }
            }
        }
        task.group = "verik"
        task.outputs.cacheIf { false }
        task.outputs.upToDateWhen { false }
    }
}
