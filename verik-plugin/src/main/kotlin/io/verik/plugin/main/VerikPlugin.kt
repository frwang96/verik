/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.compiler.main.VerikCompilerException
import io.verik.compiler.main.VerikCompilerMain
import io.verik.importer.main.VerikImporterException
import io.verik.importer.main.VerikImporterMain
import io.verik.plugin.`object`.VerikDomainObject
import io.verik.plugin.`object`.VerikDomainObjectImpl
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPluginExtension

/**
 * Top level plugin class that registers the gradle tasks.
 */
@Suppress("unused")
class VerikPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.dependencies.add("implementation", "io.verik:verik-core:${ConfigUtil.getVersion()}")
        val extension = VerikDomainObjectImpl()
        project.extensions.add(VerikDomainObject::class.java, "verik", extension)

        val verikImportTask = createVerikImportTask(project, extension)
        val verikCompileTask = createVerikCompileTask(project, extension)
        val verikTask = createVerikTask(project, extension)
        verikCompileTask.dependsOn(verikImportTask)
        verikTask.dependsOn(verikCompileTask)

        val javaPluginExtension = project.extensions.getByType(JavaPluginExtension::class.java)
        javaPluginExtension.sourceCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.targetCompatibility = JavaVersion.VERSION_1_8
        javaPluginExtension.sourceSets.getByName("main").java {
            it.srcDir(VerikImporterConfigBuilder.getBuildDir(project).resolve("src"))
        }
    }

    @Suppress("DuplicatedCode")
    private fun createVerikImportTask(project: Project, extension: VerikDomainObjectImpl): Task {
        val task = project.tasks.create("verikImport") {
            it.doLast(VerikImportAction(project, extension))
        }
        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
        task.inputs.property("maxErrorCount", { extension.maxErrorCount })
        task.inputs.property("debug", { extension.debug })
        task.inputs.property("includeDirs", { extension.import.includeDirs.joinToString() })
        task.inputs.property("enablePreprocessorOutput", { extension.import.enablePreprocessorOutput })
        task.inputs.property("suppressedWarnings", { extension.import.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.import.promotedWarnings })

        task.inputs.files({ extension.import.importedFiles })
        task.outputs.dirs({
            if (extension.import.importedFiles.isNotEmpty()) {
                VerikImporterConfigBuilder.getBuildDir(project)
            } else listOf()
        })
        return task
    }

    @Suppress("DuplicatedCode")
    private fun createVerikCompileTask(project: Project, extension: VerikDomainObjectImpl): Task {
        val task = project.tasks.create("verikCompile") {
            it.doLast(VerikCompileAction(project, extension))
        }

        task.group = "verik"
        task.inputs.property("toolchain", { ConfigUtil.getToolchain() })
        task.inputs.property("maxErrorCount", { extension.maxErrorCount })
        task.inputs.property("debug", { extension.debug })
        task.inputs.property("timescale", { extension.compile.timescale })
        task.inputs.property("entryPoints", { extension.compile.entryPoints })
        task.inputs.property("enableDeadCodeElimination", { extension.compile.enableDeadCodeElimination })
        task.inputs.property("labelLines", { extension.compile.labelLines })
        task.inputs.property("indentLength", { extension.compile.indentLength })
        task.inputs.property("wrapLength", { extension.compile.wrapLength })
        task.inputs.property("suppressedWarnings", { extension.compile.suppressedWarnings })
        task.inputs.property("promotedWarnings", { extension.compile.promotedWarnings })

        task.inputs.files({ VerikCompilerConfigBuilder.getSourceSetConfigs(project).flatMap { it.files } })
        task.outputs.dir(VerikCompilerConfigBuilder.getBuildDir(project))
        return task
    }

    private fun createVerikTask(project: Project, extension: VerikDomainObjectImpl): Task {
        val task = project.tasks.create("verik") {
            it.doLast(VerikAction(project, extension))
        }

        task.group = "verik"
        return task
    }

    private class VerikImportAction(
        private val project: Project,
        private val extension: VerikDomainObjectImpl
    ) : Action<Task> {

        override fun execute(task: Task) {
            try {
                VerikImporterMain.run(VerikImporterConfigBuilder.getConfig(project, extension))
            } catch (exception: VerikImporterException) {
                throw GradleException("Verik import failed")
            }
        }
    }

    private class VerikCompileAction(
        private val project: Project,
        private val extension: VerikDomainObjectImpl
    ) : Action<Task> {

        override fun execute(task: Task) {
            try {
                VerikCompilerMain.run(VerikCompilerConfigBuilder.getConfig(project, extension))
            } catch (exception: VerikCompilerException) {
                throw GradleException("Verik compilation failed")
            }
        }
    }

    private class VerikAction(
        private val project: Project,
        private val extension: VerikDomainObjectImpl
    ) : Action<Task> {

        override fun execute(task: Task) {}
    }
}
