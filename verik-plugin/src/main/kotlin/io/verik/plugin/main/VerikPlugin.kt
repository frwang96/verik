/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.compiler.main.VerikCompilerException
import io.verik.compiler.main.VerikCompilerMain
import io.verik.importer.main.VerikImporterException
import io.verik.importer.main.VerikImporterMain
import io.verik.plugin.domain.VerikDomainObject
import io.verik.plugin.domain.VerikDomainObjectImpl
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

    private fun createVerikImportTask(project: Project, extension: VerikDomainObjectImpl): Task {
        val task = project.tasks.create("verikImport") {
            it.doLast(VerikImportAction(project, extension))
        }
        task.group = "verik"
        task.inputs.property("hash", { VerikImporterConfigBuilder.getHash(project, extension) })
        task.inputs.files({ extension.importDomainObject.importedFiles })
        task.outputs.dirs({
            if (extension.importDomainObject.importedFiles.isNotEmpty()) {
                VerikImporterConfigBuilder.getBuildDir(project)
            } else listOf()
        })
        return task
    }

    private fun createVerikCompileTask(project: Project, extension: VerikDomainObjectImpl): Task {
        val task = project.tasks.create("verikCompile") {
            it.doLast(VerikCompileAction(project, extension))
        }

        task.group = "verik"
        task.inputs.property("hash", { VerikCompilerConfigBuilder.getHash(project, extension) })
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

        override fun execute(task: Task) {
            try {
                VerikTargetMain.run(project, extension)
            } catch (exception: Exception) {
                if (exception is VerikTargetException) {
                    println("e: Target ${exception.targetName}: ${exception.message}")
                } else {
                    println("e: Uncaught exception: ${exception.message}")
                }
                throw GradleException("Verik target generation failed")
            }
        }
    }
}
