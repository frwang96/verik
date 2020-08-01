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

data class ProjectConfig(
        val projectDir: File,
        val project: String,
        val buildDir: File,
        val labelLineNumbers: Boolean,
        val vivado: VivadoProjectConfig,
        val sourceFile: File
) {

    val destFile = buildDir.resolve("src/${sourceFile.nameWithoutExtension}.sv")
    val sourceListFile = buildDir.resolve("srcs.txt")

    companion object {

        operator fun invoke(configPath: String): ProjectConfig {
            val configFile = File(configPath).absoluteFile
            if (!configFile.exists()) throw IllegalArgumentException("project configuration $configPath not found")

            val config = Yaml.default.parse(YamlProjectConfig.serializer(), configFile.readText())

            val projectDir = configFile.parentFile
            val buildDir = projectDir.resolve(config.buildDir)
            val vivado = VivadoProjectConfig(projectDir, buildDir, config.vivado)
            val sourceFile = projectDir.resolve(config.src)
            return ProjectConfig(projectDir, config.project, buildDir, config.labelLineNumbers, vivado, sourceFile)
        }
    }
}

data class VivadoProjectConfig(
        val part: String,
        val constraints: File?,
        val tclFile: File
) {

    companion object {

        operator fun invoke(projectDir: File, buildDir: File, config: VivadoYamlProjectConfig): VivadoProjectConfig {
            val constraints = if (config.constraints != null) {
                projectDir.resolve(config.constraints).also {
                    if (!it.exists()) throw IllegalArgumentException("constraints ${it.relativeTo(projectDir)} not found")
                }
            } else null
            return VivadoProjectConfig(config.part, constraints, buildDir.resolve("build.tcl"))
        }
    }
}