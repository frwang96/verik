/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.plugin.config

import io.verik.plugin.domain.DsimSimDomainObjectImpl
import io.verik.plugin.domain.DsimTargetDomainObjectImpl
import io.verik.plugin.domain.IverilogTargetDomainObjectImpl
import io.verik.plugin.domain.TargetDomainObject
import io.verik.plugin.domain.VerikDomainObjectImpl
import io.verik.plugin.domain.VivadoTargetDomainObjectImpl
import io.verik.plugin.domain.XrunTargetDomainObjectImpl
import io.verik.plugin.main.VerikTargetException
import java.nio.file.Path

/**
 * Factory class that builds [TargetConfig] objects from domain objects.
 */
object TargetConfigBuilder {

    private val nameRegex = Regex("[_\\-a-zA-Z0-9]*")

    fun getTargetConfigs(projectConfig: ProjectConfig, extension: VerikDomainObjectImpl): List<TargetConfig> {
        val targetNameSet = HashSet<String>()
        extension.targetDomainObjects.forEach {
            if (it.name in targetNameSet) {
                throw VerikTargetException(it, "Target with this name has already been registered")
            }
            targetNameSet.add(it.name)
            if (!it.name.matches(nameRegex)) {
                throw VerikTargetException(it, "Illegal target name")
            }
        }

        val targetConfigs = ArrayList<TargetConfig>()
        extension.targetDomainObjects.forEach {
            val targetConfig = when (it) {
                is DsimTargetDomainObjectImpl -> getDsimTargetConfig(projectConfig, it)
                is IverilogTargetDomainObjectImpl -> getIverilogTargetConfig(projectConfig, it)
                is VivadoTargetDomainObjectImpl -> getVivadoTargetConfig(projectConfig, it)
                is XrunTargetDomainObjectImpl -> getXrunTargetConfig(projectConfig, it)
                else -> throw VerikTargetException(it, "Unknown target type")
            }
            targetConfigs.add(targetConfig)
        }
        return targetConfigs
    }

    private fun getDsimTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: DsimTargetDomainObjectImpl,
    ): DsimTargetConfig {
        if (domainObject.compileTops.isEmpty()) {
            throw VerikTargetException(domainObject, "Property not provided: compileTops")
        }
        val simConfigs = domainObject.simDomainObjects.map { getDsimSimConfig(domainObject, it) }
        return DsimTargetConfig(
            projectConfig = projectConfig,
            name = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            compileTops = domainObject.compileTops,
            extraFiles = domainObject.extraFiles,
            extraIncludeDirs = domainObject.extraIncludeDirs,
            dpiLibs = domainObject.dpiLibs,
            simConfigs = simConfigs
        )
    }

    private fun getDsimSimConfig(
        targetDomainObject: DsimTargetDomainObjectImpl,
        simDomainObject: DsimSimDomainObjectImpl
    ): DsimSimConfig {
        if (simDomainObject.name.isBlank()) {
            throw VerikTargetException(targetDomainObject, "Sim property not provided: name")
        }
        val simTop = if (simDomainObject.simTop.isBlank()) {
            if (targetDomainObject.compileTops.size != 1) {
                throw VerikTargetException(targetDomainObject, "Sim property not provided: simTop")
            }
            targetDomainObject.compileTops.first()
        } else {
            if (simDomainObject.simTop !in targetDomainObject.compileTops) {
                throw VerikTargetException(targetDomainObject, "Sim top not in compile tops: ${simDomainObject.simTop}")
            }
            simDomainObject.simTop
        }
        return DsimSimConfig(
            name = simDomainObject.name,
            simTop = simTop,
            plusArgs = simDomainObject.plusArgs
        )
    }

    private fun getIverilogTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: IverilogTargetDomainObjectImpl
    ): IverilogTargetConfig {
        if (domainObject.top.isBlank()) {
            throw VerikTargetException(domainObject, "Property not provided: top")
        }
        return IverilogTargetConfig(
            projectConfig = projectConfig,
            name = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            top = domainObject.top
        )
    }

    private fun getVivadoTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: VivadoTargetDomainObjectImpl
    ): VivadoTargetConfig {
        if (domainObject.part.isBlank()) {
            throw VerikTargetException(domainObject, "Property not provided: part")
        }

        return VivadoTargetConfig(
            projectConfig = projectConfig,
            name = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            part = domainObject.part,
            ipConfigFiles = domainObject.ipConfigFiles
        )
    }

    private fun getXrunTargetConfig(
        projectConfig: ProjectConfig,
        domainObject: XrunTargetDomainObjectImpl
    ): XrunTargetConfig {
        if (domainObject.top.isBlank()) {
            throw VerikTargetException(domainObject, "Property not provided: top")
        }
        return XrunTargetConfig(
            projectConfig = projectConfig,
            name = domainObject.name,
            buildDir = getBuildDir(projectConfig, domainObject),
            top = domainObject.top
        )
    }

    private fun getBuildDir(projectConfig: ProjectConfig, domainObject: TargetDomainObject): Path {
        return projectConfig.buildDir.resolve(domainObject.name)
    }
}
