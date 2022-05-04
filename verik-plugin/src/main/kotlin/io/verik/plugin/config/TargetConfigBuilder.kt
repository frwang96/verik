/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import io.verik.plugin.domain.AuroraTargetDomainObjectImpl
import io.verik.plugin.domain.VerikDomainObjectImpl
import io.verik.plugin.domain.VivadoTargetDomainObjectImpl
import io.verik.plugin.main.ConfigUtil
import org.gradle.api.Project

/**
 * Factory class that builds [TargetConfig] objects from domain objects.
 */
object TargetConfigBuilder {

    fun getTargetConfigs(project: Project, extension: VerikDomainObjectImpl): List<TargetConfig> {
        val targetConfigs = ArrayList<TargetConfig>()
        extension.auroraDomainObjects.forEach {
            targetConfigs.add(getAuroraTargetConfig(project, it))
        }
        extension.vivadoDomainObjects.forEach {
            targetConfigs.add(getVivadoTargetConfig(project, it))
        }
        return targetConfigs
    }

    private fun getAuroraTargetConfig(
        project: Project,
        domainObject: AuroraTargetDomainObjectImpl,
    ): AuroraTargetConfig {
        return AuroraTargetConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            targetName = domainObject.name,
            compileTops = domainObject.compileTops,
            dpiLibs = domainObject.dpiLibs
        )
    }

    private fun getVivadoTargetConfig(
        project: Project,
        domainObject: VivadoTargetDomainObjectImpl
    ): VivadoTargetConfig {
        return VivadoTargetConfig(
            toolchain = ConfigUtil.getToolchain(),
            timestamp = ConfigUtil.getTimestamp(),
            projectName = project.name,
            targetName = domainObject.name,
            part = domainObject.part,
            ipConfigFiles = domainObject.ipConfigFiles
        )
    }
}
