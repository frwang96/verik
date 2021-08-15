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

import io.verik.compiler.main.Main
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(KotlinPluginWrapper::class.java)
        val extension = project.extensions.create("verik", VerikPluginExtension::class.java)
        val task = project.tasks.create("verik") {
            it.doLast {
                try {
                    Main.run(Config(project, extension))
                } catch (exception: Exception) {
                    if (exception is GradleException) {
                        throw exception
                    } else {
                        print("e: Unhandled exception: ${exception::class.simpleName}")
                        if (exception.message != null) print(": ${exception.message}")
                        println()
                        if (extension.debug) {
                            exception.stackTrace.forEach { stackTraceElement ->
                                if (stackTraceElement.className.startsWith("io.verik"))
                                    println("at $stackTraceElement")
                            }
                        }
                        throw GradleException("Verik compilation failed")
                    }
                }
            }
        }

        task.group = "verik"
        task.inputs.property("version", { Config.getVersion(project) })
        task.inputs.property("top", { extension.top })
        task.inputs.property("verbose", { extension.verbose })
        task.inputs.property("debug", { extension.debug })
        task.inputs.property("labelLines", { extension.labelLines })
        task.inputs.property("alignLength", { extension.alignLength })
        task.inputs.property("wrapLength", { extension.wrapLength })
        task.inputs.property("indentLength", { extension.indentLength })
        Config.getInputFiles(project).forEach { task.inputs.file(it) }
        task.outputs.dir(Config.getBuildDir(project))
    }
}