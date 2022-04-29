/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.compiler.main.VerikCompilerException
import io.verik.compiler.main.VerikCompilerMain
import io.verik.importer.main.VerikImporterException
import io.verik.importer.main.VerikImporterMain
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension

/**
 * Top level plugin class that registers the verik and verikImport gradle tasks for the compiler and importer
 * respectively.
 */
@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.dependencies.add("implementation", "io.verik:verik-core:${ConfigUtil.getVersion()}")
        val verikBuildTask = createVerikBuildTask(project)
        val verikCompileTask = createVerikCompileTask(project)
        val verikImportTask = createVerikImportTask(project)
        verikBuildTask.dependsOn(verikCompileTask)
        verikCompileTask.dependsOn(verikImportTask)

        val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        javaPluginExtension.sourceCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.targetCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.sourceSets.getByName("main").java {
            it.srcDir(VerikImporterConfigBuilder.getBuildDir(project).resolve("src"))
        }
    }

    private fun createVerikBuildTask(project: Project): Task {
        val targetContainer = TargetContainer(
            project.objects.polymorphicDomainObjectContainer(TargetDomainObject::class.java)
        )
        project.extensions.add("verikBuild", targetContainer)
        val task = project.tasks.create("verikBuild") {
            it.doLast(VerikBuildAction(project, targetContainer))
        }

        task.group = "verik"
        return task
    }

    @Suppress("DuplicatedCode")
    private fun createVerikCompileTask(project: Project): Task {
        val extension = project.extensions.create("verikCompile", VerikCompilerPluginExtension::class.java)
        val task = project.tasks.create("verikCompile") {
            it.doLast(VerikCompileAction(project, extension))
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
        task.inputs.files({ VerikCompilerConfigBuilder.getSourceSetConfigs(project).flatMap { it.files } })
        task.outputs.dir(VerikCompilerConfigBuilder.getBuildDir(project))
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

    private class VerikBuildAction(
        private val project: Project,
        private val targetContainer: TargetContainer
    ) : Action<Task> {

        override fun execute(task: Task) {}
    }

    private class VerikCompileAction(
        private val project: Project,
        private val extension: VerikCompilerPluginExtension
    ) : Action<Task> {

        override fun execute(task: Task) {
            try {
                VerikCompilerMain.run(VerikCompilerConfigBuilder.getConfig(project, extension))
            } catch (exception: VerikCompilerException) {
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
                VerikImporterMain.run(VerikImporterConfigBuilder.getConfig(project, extension))
            } catch (exception: VerikImporterException) {
                throw GradleException("Verik import failed")
            }
        }
    }
}
