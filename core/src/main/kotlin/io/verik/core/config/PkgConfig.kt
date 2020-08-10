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
        val outDir: File,
        val pkgKt: String,
        val pkgSv: String?,
        val files: List<FileConfig>
) {

    val header = dir.resolve("headers.kt")

    companion object {

        operator fun invoke(sourceRoot: File, buildCopyDir:File, buildOutDir: File, dir: File): PkgConfig? {
            val relativePath = dir.relativeTo(sourceRoot)
            val pkgKt = relativePath.toString().replace("/", ".")
            val copyDir = buildCopyDir.resolve(relativePath)
            val outDir = buildOutDir.resolve(relativePath)
            return if (dir.isDirectory) {
                val files = dir.listFiles()?.apply { sort() }?.filter { it.extension == "kt" && it.name != "headers.kt" }
                if (files != null && files.isNotEmpty()) {
                    val configFile = dir.resolve("vkpkg.yaml")
                    val config = if (configFile.exists()) {
                        Yaml.default.parse(YamlPkgConfig.serializer(), configFile.readText())
                    } else {
                        YamlPkgConfig(null)
                    }
                    val fileConfigs = files.map { FileConfig(sourceRoot, buildCopyDir, buildOutDir, it) }
                    PkgConfig(dir, copyDir, outDir, pkgKt, config.pkg, fileConfigs)
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
}
