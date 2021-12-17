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
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinCommonPluginWrapper
import io.verik.compiler.main.StageSequencer as VerikStageSequencer
import io.verik.importer.main.StageSequencer as VerikImporterStageSequencer

@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.plugins.apply(KotlinCommonPluginWrapper::class.java)
        project.dependencies.add("implementation", "io.verik:verik-core:${ConfigUtil.getVersion()}")
        val verikTask = createVerikTask(project)
        val verikImportTask = createVerikImportTask(project)
        verikTask.dependsOn(verikImportTask)

        val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        javaPluginExtension.sourceCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.targetCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.sourceSets.getByName("main").java {
            it.srcDir(VerikImporterConfigBuilder.getBuildDir(project).resolve("src"))
        }
    }

    private fun createVerikTask(project: Project): Task {
        val extension = project.extensions.create("verik", VerikPluginExtension::class.java)
        val task = project.tasks.create("verik") {
            it.doLast(VerikAction(project, extension))
        }

        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
        task.inputs.property("timescale", { extension.timescale })
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
            it.doLast(VerikImportAction(project, extension))
        }
        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
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

    private class VerikAction(
        private val project: Project,
        private val extension: VerikPluginExtension
    ) : Action<Task> {

        override fun execute(task: Task) {
            try {
                val stageSequence = VerikStageSequencer.getStageSequence()
                VerikMain.run(VerikConfigBuilder.getConfig(project, extension), stageSequence)
            } catch (exception: VerikException) {
                throw GradleException("Verik compilation failed")
            }
        }
    }

    private class VerikImportAction(
        private val project: Project,
        private val extension: VerikImporterPluginExtension
    ) : Action<Task> {

        override fun execute(task: Task) {
            try {
                val stageSequence = VerikImporterStageSequencer.getStageSequence()
                VerikImporterMain.run(VerikImporterConfigBuilder.getConfig(project, extension), stageSequence)
            } catch (exception: VerikImporterException) {
                throw GradleException("Verik import failed")
            }
        }
    }
}
