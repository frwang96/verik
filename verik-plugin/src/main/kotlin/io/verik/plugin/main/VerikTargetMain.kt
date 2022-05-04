/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.main

import io.verik.plugin.config.DsimTargetConfig
import io.verik.plugin.config.ProjectConfigBuilder
import io.verik.plugin.config.TargetConfig
import io.verik.plugin.config.TargetConfigBuilder
import io.verik.plugin.domain.VerikDomainObjectImpl
import io.verik.plugin.target.DsimTargetBuilder
import org.gradle.api.Project
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

        when (targetConfig) {
            is DsimTargetConfig -> DsimTargetBuilder.build(targetConfig)
            else -> throw VerikTargetException(targetConfig, "Unknown target type")
        }
    }
}
