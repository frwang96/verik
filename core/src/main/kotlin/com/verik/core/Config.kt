package com.verik.core

import com.charleskorn.kaml.Yaml
import java.io.File

// Copyright (c) 2020 Francis Wang

class ConfigException(msg: String): Exception(msg)

data class Config(
        val configDir: File,
        val project: String,
        val buildDir: File,
        val labelLineNumbers: Boolean,
        val vivado: VivadoConfig,
        val srcFile: File
) {

    val dstFile = buildDir.resolve("src/${srcFile.nameWithoutExtension}.sv")

    companion object {

        operator fun invoke(configPath: String): Config {
            val configFile = File(configPath).absoluteFile
            if (!configFile.exists()) throw ConfigException("config file $configPath not found")

            val config = Yaml.default.parse(YamlConfig.serializer(), configFile.readText())

            val configDir = configFile.parentFile
            val buildDir = configDir.resolve(config.buildDir)
            val vivado = VivadoConfig(configDir, buildDir, config.vivado)
            val srcFile = configDir.resolve(config.src)
            return Config(configDir, config.project, buildDir, config.labelLineNumbers, vivado, srcFile)
        }
    }
}

data class VivadoConfig(
        val part: String,
        val constraints: File,
        val tclFile: File
) {

    companion object {

        operator fun invoke(configDir: File, buildDir: File, config: VivadoYamlConfig): VivadoConfig {
            val constraints = configDir.resolve(config.constraints)
            if (!constraints.exists()) throw ConfigException("constraints file ${constraints.relativeTo(configDir)} not found")
            return VivadoConfig(config.part, constraints, buildDir.resolve("build.tcl"))
        }
    }
}