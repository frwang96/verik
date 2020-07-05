package com.verik.core

import com.charleskorn.kaml.Yaml
import java.io.File

// Copyright (c) 2020 Francis Wang

class ProjConfException(msg: String): Exception(msg)

data class ProjConf(
        val projDir: File,
        val project: String,
        val buildDir: File,
        val labelLineNumbers: Boolean,
        val vivado: VivadoProjConf,
        val srcFile: File
) {

    val dstFile = buildDir.resolve("src/${srcFile.nameWithoutExtension}.sv")

    companion object {

        operator fun invoke(confPath: String): ProjConf {
            val confFile = File(confPath).absoluteFile
            if (!confFile.exists()) throw ProjConfException("project configuration file $confPath not found")

            val conf = Yaml.default.parse(YamlProjConf.serializer(), confFile.readText())

            val projDir = confFile.parentFile
            val buildDir = projDir.resolve(conf.buildDir)
            val vivado = VivadoProjConf(projDir, buildDir, conf.vivado)
            val srcFile = projDir.resolve(conf.src)
            return ProjConf(projDir, conf.project, buildDir, conf.labelLineNumbers, vivado, srcFile)
        }
    }
}

data class VivadoProjConf(
        val part: String,
        val constraints: File,
        val tclFile: File
) {

    companion object {

        operator fun invoke(projDir: File, buildDir: File, conf: VivadoYamlProjConf): VivadoProjConf {
            val constraints = projDir.resolve(conf.constraints)
            if (!constraints.exists()) throw ProjConfException("constraints file ${constraints.relativeTo(projDir)} not found")
            return VivadoProjConf(conf.part, constraints, buildDir.resolve("build.tcl"))
        }
    }
}