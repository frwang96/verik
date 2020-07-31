/*
 * Copyright 2020 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.core

import com.charleskorn.kaml.Yaml
import java.io.File

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
    val sourceListFile = buildDir.resolve("sources.txt")

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
        val constraints: File?,
        val tclFile: File
) {

    companion object {

        operator fun invoke(projDir: File, buildDir: File, conf: VivadoYamlProjConf): VivadoProjConf {
            val constraints = if (conf.constraints != null) {
                projDir.resolve(conf.constraints).also {
                    if (!it.exists()) throw ProjConfException("constraints file ${it.relativeTo(projDir)} not found")
                }
            } else null
            return VivadoProjConf(conf.part, constraints, buildDir.resolve("build.tcl"))
        }
    }
}