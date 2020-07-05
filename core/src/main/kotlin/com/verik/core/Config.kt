package com.verik.core

import com.charleskorn.kaml.Yaml
import java.io.File

// Copyright (c) 2020 Francis Wang

class ConfigException(msg: String): Exception(msg)

data class Config(val vivado: ConfigVivado, val src: File) {

    private val buildDir = File("build/verik")

    val dstDir = buildDir.resolve("src")
    val dstFile = dstDir.resolve("${src.nameWithoutExtension}.sv")

    val tclDir = buildDir
    val tclFile = buildDir.resolve("build.tcl")

    companion object {

        operator fun invoke(configPath: String): Config {
            val configFile = File(configPath)
            if (!configFile.exists()) throw ConfigException("config file ${configFile.path} not found")

            val config = Yaml.default.parse(YamlConfig.serializer(), configFile.readText())

            val vivado = ConfigVivado(config.vivado)
            val src = configFile.resolveSibling(config.src)
            return Config(vivado, src)
        }
    }
}

data class ConfigVivado(val part: String, val constraints: File) {

    companion object {

        operator fun invoke(configVivado: YamlConfigVivado): ConfigVivado {
            val constraints = File(configVivado.constraints)
            if (!constraints.exists()) throw ConfigException("constraints file ${constraints.path} not found")
            return ConfigVivado(configVivado.part, constraints)
        }
    }
}