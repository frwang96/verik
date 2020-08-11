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
import io.verik.core.StatusPrinter
import io.verik.core.symbol.FileTable
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConfigLoader {

    companion object {

        fun loadProjectConfig(configPath: String): ProjectConfig {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
            val timeString = current.format(formatter)

            val configFile = File(configPath).absoluteFile
            val projectDir = configFile.parentFile
            if (!configFile.exists()) {
                throw IllegalArgumentException("project configuration $configPath not found")
            }

            StatusPrinter.info("loading project configuration ${configFile.relativeTo(projectDir)}")

            val config = Yaml.default.parse(YamlProjectConfig.serializer(), configFile.readText())

            val buildDir = projectDir.resolve(config.buildDir ?: "build/verik")
            val buildCopyDir = buildDir.resolve("src")
            val buildOutDir = buildDir.resolve("out")

            val gradle = loadProjectGradleConfig(projectDir, config.gradleDir)
            val compile = loadProjectCompileConfig(config.compile)
            val stubs = loadProjectStubsConig(projectDir, config.stubs)
            val fileTable = loadFileTable(configFile, projectDir, buildCopyDir, buildOutDir, config.src)

            return ProjectConfig(
                    timeString,
                    configFile,
                    projectDir,
                    config.project,
                    buildDir,
                    buildCopyDir,
                    buildOutDir,
                    gradle,
                    compile,
                    stubs,
                    fileTable
            )
        }

        private fun loadProjectGradleConfig(
                projectDir: File,
                gradleDir: String?
        ): ProjectGradleConfig {
            val dir = projectDir.resolve(gradleDir ?: ".")
            if (!dir.exists()) throw IllegalArgumentException("gradle directory ${dir.relativeTo(projectDir)} not found")

            val wrapperSh = dir.resolve("gradlew")
            if (!wrapperSh.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

            val wrapperBat = dir.resolve("gradlew.bat")
            if (!wrapperBat.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

            return ProjectGradleConfig(dir, wrapperSh, wrapperBat)
        }

        private fun loadProjectCompileConfig(
                config: YamlCompileConfig?
        ): ProjectCompileConfig {
            return if (config != null) {
                ProjectCompileConfig(
                        config.top,
                        CompileScopeType(config.scope),
                        config.labelLines ?: true
                )
            } else ProjectCompileConfig(null, CompileScopeType(null), true)
        }

        private fun loadProjectStubsConig(
                projectDir: File,
                config: YamlStubsConfig?
        ): ProjectStubsConfig? {
            return if (config?.main != null) {
                val jarPath = (config.jar) ?: "build/libs/out.jar"
                val jar = projectDir.resolve(jarPath)
                if (jar.extension != "jar") throw IllegalArgumentException("invalid jar ${jar.relativeTo(projectDir)}")
                ProjectStubsConfig(config.main, jar)
            } else null
        }

        private fun loadFileTable(
                configFile: File,
                projectDir: File,
                buildCopyDir: File,
                buildOutDir: File,
                config: YamlSourceConfig?
        ): FileTable {
            val root = projectDir.resolve(config?.root ?: "src/main/kotlin")
            if (!root.exists()) {
                throw IllegalArgumentException("source root ${root.relativeTo(projectDir)} not found")
            }

            val pkgStrings = config?.pkgs ?: listOf("")
            for (pkgString in pkgStrings) {
                if (pkgStrings.any { it.startsWith("$pkgString.") }) {
                    StatusPrinter.warning("redundant package $pkgString in ${configFile.relativeTo(projectDir)}")
                }
            }

            val pkgs = ArrayList<Pair<PkgConfig, List<FileConfig>>>()
            val pkgSet = HashSet<String>()
            for (pkgString in pkgStrings) {
                val pkgFile = root.resolve(pkgString.replace(".", "/"))
                if (!pkgFile.exists()) throw IllegalArgumentException("package $pkgString not found")

                pkgFile.walk().forEach { file ->
                    val pkgConfigs = loadPkgConfigs(root, buildCopyDir, buildOutDir, file)
                    if (pkgConfigs != null) {
                        val pkgName = pkgConfigs.first.pkgKt
                        if (!pkgSet.contains(pkgName)) {
                            pkgs.add(pkgConfigs)
                            pkgSet.add(pkgName)
                        }
                    }
                }
            }

            return FileTable(pkgs)
        }

        private fun loadPkgConfigs(
                sourceRoot: File,
                buildCopyDir: File,
                buildOutDir: File,
                dir: File
        ): Pair<PkgConfig, List<FileConfig>>? {
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
                    val pkgSv = config.pkg
                    val fileConfigs = files.map { loadFileConfig(
                            sourceRoot,
                            buildCopyDir,
                            buildOutDir,
                            it,
                            pkgKt,
                            pkgSv
                    ) }
                    val pkgConfig = PkgConfig(
                            dir,
                            copyDir,
                            outDir,
                            pkgKt,
                            pkgSv
                    )
                    Pair(pkgConfig, fileConfigs)
                } else {
                    null
                }
            } else {
                null
            }
        }

        private fun loadFileConfig(
                sourceRoot: File,
                buildCopyDir: File,
                buildOutDir: File,
                file: File,
                pkgKt: String,
                pkgSv: String?
        ): FileConfig {
            val relativePath = file.relativeTo(sourceRoot)
            val copyFile = buildCopyDir.resolve(relativePath)
            val parent = buildOutDir.resolve(relativePath).parentFile
            val name = "${file.nameWithoutExtension}.sv"
            val outFile = parent.resolve(name)
            return FileConfig(
                    file,
                    copyFile,
                    outFile,
                    pkgKt,
                    pkgSv
            )
        }
    }
}