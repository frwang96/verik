/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import io.verik.plugin.main.ConfigUtil
import org.gradle.api.Project

/**
 * Factory class that builds the [ProjectConfig].
 */
object ProjectConfigBuilder {

    fun getProjectConfig(project: Project): ProjectConfig {
        return ProjectConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            buildDir = project.buildDir.toPath()
        )
    }
}
