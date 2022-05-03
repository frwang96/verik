/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.importer.main.VerikImporterConfig
import io.verik.plugin.`object`.VerikDomainObjectImpl
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
            importedFiles = extension.import.importedFiles,
            includeDirs = extension.import.includeDirs,
            enablePreprocessorOutput = extension.import.enablePreprocessorOutput,
            suppressedWarnings = extension.import.suppressedWarnings,
            promotedWarnings = extension.import.promotedWarnings,
            maxErrorCount = extension.maxErrorCount,
            debug = extension.debug
        )
    }

    fun getBuildDir(project: Project): Path {
        return project.buildDir.resolve("verik-import").toPath()
    }
}
