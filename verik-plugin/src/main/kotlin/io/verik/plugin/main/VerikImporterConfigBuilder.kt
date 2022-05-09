/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.importer.main.VerikImporterConfig
import io.verik.plugin.domain.VerikDomainObjectImpl
import org.gradle.api.Project
import java.nio.file.Path

/**
 * Factory class that builds a [VerikImporterConfig] object.
 */
object VerikImporterConfigBuilder {

    fun getConfig(project: Project, extension: VerikDomainObjectImpl): VerikImporterConfig {
        return VerikImporterConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            buildDir = getBuildDir(project),
            importedFiles = extension.importDomainObject.importedFiles,
            includeDirs = extension.importDomainObject.includeDirs,
            enablePreprocessorOutput = extension.importDomainObject.enablePreprocessorOutput,
            suppressedWarnings = extension.importDomainObject.suppressedWarnings,
            promotedWarnings = extension.importDomainObject.promotedWarnings,
            maxErrorCount = extension.maxErrorCount,
            debug = extension.debug
        )
    }

    @Suppress("DuplicatedCode")
    fun getHash(project: Project, extension: VerikDomainObjectImpl): Int {
        val config = getConfig(project, extension)
        val builder = StringBuilder()
        builder.appendLine(config.toolchain)
        builder.appendLine(config.projectName)
        builder.appendLine(config.buildDir)
        builder.appendLine(config.includeDirs)
        builder.appendLine(config.enablePreprocessorOutput)
        builder.appendLine(config.suppressedWarnings.joinToString())
        builder.appendLine(config.promotedWarnings.joinToString())
        builder.appendLine(config.maxErrorCount)
        builder.appendLine(config.debug)
        return builder.toString().hashCode()
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("import").toPath()
    }
}
