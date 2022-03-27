/*
 * SPDX-License-Identifier: Apache-2.0
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
import io.verik.compiler.main.StageSequencer as VerikStageSequencer
import io.verik.importer.main.StageSequencer as VerikImporterStageSequencer

@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
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

    @Suppress("DuplicatedCode")
    private fun createVerikTask(project: Project): Task {
        val extension = project.extensions.create("verik", VerikPluginExtension::class.java)
        val task = project.tasks.create("verik") {
            it.doLast(VerikAction(project, extension))
        }

        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
        task.inputs.property("timescale", { extension.timescale })
        task.inputs.property("entryPoints", { extension.entryPoints })
        task.inputs.property("enableDeadCodeElimination", { extension.enableDeadCodeElimination })
        task.inputs.property("labelLines", { extension.labelLines })
        task.inputs.property("indentLength", { extension.indentLength })
        task.inputs.property("wrapLength", { extension.wrapLength })
        task.inputs.property("suppressedWarnings", { extension.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.promotedWarnings })
        task.inputs.property("maxErrorCount", { extension.maxErrorCount })
        task.inputs.property("debug", { extension.debug })
        task.inputs.files({ VerikConfigBuilder.getSourceSetConfigs(project).flatMap { it.files } })
        task.outputs.dir(VerikConfigBuilder.getBuildDir(project))
        return task
    }

    @Suppress("DuplicatedCode")
    private fun createVerikImportTask(project: Project): Task {
        val extension = project.extensions.create("verikImport", VerikImporterPluginExtension::class.java)
        val task = project.tasks.create("verikImport") {
            it.doLast(VerikImportAction(project, extension))
        }
        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
        task.inputs.property("includeDirs", { extension.includeDirs.joinToString() })
        task.inputs.property("enablePreprocessorOutput", { extension.enablePreprocessorOutput })
        task.inputs.property("suppressedWarnings", { extension.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.promotedWarnings })
        task.inputs.property("maxErrorCount", { extension.maxErrorCount })
        task.inputs.property("debug", { extension.debug })
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
