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
        val copyDir: File,
        val pkgName: String?,
        val sources: List<SourceConfig>
) {

    val header = dir.resolve("headers.kt")

    companion object {

        operator fun invoke(sourceRoot: File, buildCopyDir:File, buildOutDir: File, dir: File): PkgConfig? {
            val relativePath = dir.relativeTo(sourceRoot)
            val copyDir = buildCopyDir.resolve(relativePath)
            return if (dir.isDirectory) {
                val sources = dir.listFiles()?.filter { it.extension == "kt" && it.name != "headers.kt" }
                if (sources != null && sources.isNotEmpty()) {
                    val configFile = dir.resolve("vkconf.yaml")
                    val config = if (configFile.exists()) {
                        Yaml.default.parse(YamlPackageConfig.serializer(), configFile.readText())
                    } else {
                        YamlPackageConfig(null)
                    }
                    PkgConfig(dir, copyDir, config.pkg, sources.map { SourceConfig(sourceRoot, buildCopyDir, buildOutDir, it) })
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
        val copy: File,
        val out: File
) {

    companion object {

        operator fun invoke(sourceRoot: File, buildCopyDir: File, buildOutDir: File, source: File): SourceConfig {
            val relativePath = source.relativeTo(sourceRoot)
            val copy = buildCopyDir.resolve(relativePath)
            val parent = buildOutDir.resolve(relativePath).parentFile
            val name = "${source.nameWithoutExtension}.sv"
            val out = parent.resolve(name)
            return SourceConfig(source, copy, out)
        }
    }
}