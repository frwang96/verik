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

package io.verik.core.config

import com.charleskorn.kaml.Yaml
import java.io.File

data class PkgConfig(
        val dir: File,
        val pkgName: String?,
        val sources: List<SourceConfig>
) {

    companion object {

        operator fun invoke(sourceRoot: File, buildSourceDir:File, dir: File): PkgConfig? {
            return if (dir.isDirectory) {
                val sources = dir.listFiles()?.filter { it.extension == "kt" }
                if (sources != null && sources.isNotEmpty()) {
                    val configFile = dir.resolve("vkconf.yaml")
                    val config = if (configFile.exists()) {
                        Yaml.default.parse(YamlPackageConfig.serializer(), configFile.readText())
                    } else {
                        YamlPackageConfig(null)
                    }
                    PkgConfig(dir, config.pkg, sources.map { SourceConfig(sourceRoot, buildSourceDir, it) })
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
}

data class SourceConfig(
        val source: File,
        val dest: File
) {

    companion object {

        operator fun invoke(sourceRoot: File, buildSourceDir: File, source: File): SourceConfig {
            val relativePath = source.relativeTo(sourceRoot)
            val parent = buildSourceDir.resolve(relativePath).parentFile
            val name = "${source.nameWithoutExtension}.sv"
            val dest = parent.resolve(name)
            return SourceConfig(source, dest)
        }
    }
}