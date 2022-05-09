/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.plugin.config.DsimTargetConfig
import io.verik.plugin.config.IverilogTargetConfig
import io.verik.plugin.config.ProjectConfigBuilder
import io.verik.plugin.config.TargetConfig
import io.verik.plugin.config.TargetConfigBuilder
import io.verik.plugin.domain.VerikDomainObjectImpl
import io.verik.plugin.target.DsimTargetBuilder
import io.verik.plugin.target.IverilogTargetBuilder
import org.gradle.api.Project
import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

/**
 *  Entry point for target generation.
 */
object VerikTargetMain {

    fun run(project: Project, extension: VerikDomainObjectImpl) {
        val projectConfig = ProjectConfigBuilder.getProjectConfig(project)
        val targetConfigs = TargetConfigBuilder.getTargetConfigs(projectConfig, extension)
        targetConfigs.forEach { generateTarget(it) }
    }

    private fun generateTarget(targetConfig: TargetConfig) {
        if (targetConfig.buildDir.exists()) {
            targetConfig.buildDir.toFile().deleteRecursively()
        }
        targetConfig.buildDir.createDirectories()

        val textFiles = when (targetConfig) {
            is DsimTargetConfig -> listOf(DsimTargetBuilder.build(targetConfig))
            is IverilogTargetConfig -> listOf(IverilogTargetBuilder.build(targetConfig))
            else -> throw VerikTargetException(targetConfig, "Unknown target type")
        }

        try {
            textFiles.forEach {
                Files.createDirectories(it.path.parent)
                Files.writeString(it.path, it.content)
            }
        } catch (exception: Exception) {
            throw VerikTargetException(targetConfig, "Unable to write files")
        }
    }
}
