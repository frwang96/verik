/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import io.verik.plugin.domain.DsimTargetDomainObjectImpl
import io.verik.plugin.domain.TargetDomainObject
import io.verik.plugin.domain.VerikDomainObjectImpl
import io.verik.plugin.domain.VivadoTargetDomainObjectImpl
import io.verik.plugin.main.VerikTargetException
import java.nio.file.Path

/**
 * Factory class that builds [TargetConfig] objects from domain objects.
 */
object TargetConfigBuilder {

    private val nameRegex = Regex("[_\\-a-zA-Z0-9]*")

    fun getTargetConfigs(projectConfig: ProjectConfig, extension: VerikDomainObjectImpl): List<TargetConfig> {
        val targetConfigs = ArrayList<TargetConfig>()
        extension.dsimDomainObjects.forEach {
            targetConfigs.add(getDsimTargetConfig(projectConfig, it))
        }
        extension.vivadoDomainObjects.forEach {
            targetConfigs.add(getVivadoTargetConfig(projectConfig, it))
        }
        check(targetConfigs)
        return targetConfigs
    }

    private fun getDsimTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: DsimTargetDomainObjectImpl,
    ): DsimTargetConfig {
        val targetConfig = DsimTargetConfig(
            projectConfig = projectConfig,
            targetName = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            compileTops = domainObject.compileTops,
            dpiLibs = domainObject.dpiLibs
        )

        if (targetConfig.compileTops.isEmpty()) {
            throw VerikTargetException(targetConfig, "Property not provided: compileTops")
        }

        return targetConfig
    }

    private fun getVivadoTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: VivadoTargetDomainObjectImpl
    ): VivadoTargetConfig {
        val targetConfig = VivadoTargetConfig(
            projectConfig = projectConfig,
            targetName = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            part = domainObject.part,
            ipConfigFiles = domainObject.ipConfigFiles
        )

        if (targetConfig.part.isBlank()) {
            throw VerikTargetException(targetConfig, "Property not provided: part")
        }

        return targetConfig
    }

    private fun getBuildDir(projectConfig: ProjectConfig, domainObject: TargetDomainObject): Path {
        return projectConfig.buildDir.resolve(domainObject.name)
    }

    private fun check(targetConfigs: List<TargetConfig>) {
        val targetNameSet = HashSet<String>()
        targetConfigs.forEach {
            if (it.targetName in targetNameSet) {
                throw VerikTargetException(it, "Target with this name has already been registered")
            }
            targetNameSet.add(it.targetName)
            if (!it.targetName.matches(nameRegex)) {
                throw VerikTargetException(it, "Illegal target name")
            }
        }
    }
}
