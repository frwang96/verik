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
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ProjectConfig(
        val timeString: String,
        val configFile: File,
        val projectDir: File,
        val project: String,
        val buildDir: File,
        val buildCopyDir: File,
        val buildOutDir:File,
        val pkgs: List<PkgConfig>,
        val top: String?,
        val labelLines: Boolean,
        val gradle: GradleConfig,
        val stubsMain: String?
) {

    val configCopy = buildDir.resolve("vkproject.yaml")
    val orderFile = buildDir.resolve("order.txt")
    val stubsFile = buildDir.resolve("stubs.txt")

    companion object {

        operator fun invoke(configPath: String): ProjectConfig {
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

            val sourceRoot = projectDir.resolve(config.srcRoot ?: "src/main/kotlin")
            if (!sourceRoot.exists()) {
                throw IllegalArgumentException("source root ${sourceRoot.relativeTo(projectDir)} not found")
            }

            val pkgs = getPkgs(configFile, projectDir, sourceRoot, buildCopyDir, buildOutDir, config.srcPkgs ?: listOf(""))

            val labelLines = config.labelLines ?: true
            val gradle = GradleConfig(projectDir, config.gradle)

            return ProjectConfig(
                    timeString,
                    configFile,
                    projectDir,
                    config.project,
                    buildDir,
                    buildCopyDir,
                    buildOutDir,
                    pkgs,
                    config.top,
                    labelLines,
                    gradle,
                    config.stubsMain
            )
        }

        private fun getPkgs(
                configFile: File,
                projectDir: File,
                sourceRoot: File,
                buildCopyDir: File,
                buildOutDir: File,
                sourcePkgs: List<String>
        ): List<PkgConfig> {
            val pkgs = ArrayList<PkgConfig>()

            for (sourcePkg in sourcePkgs) {
                if (sourcePkgs.any { it.startsWith("$sourcePkg.") }) {
                    StatusPrinter.warning("redundant source package $sourcePkg in ${configFile.relativeTo(projectDir)}")
                }
            }

            for (sourcePkg in sourcePkgs) {
                val sourcePkgFile = sourceRoot.resolve(sourcePkg.replace(".", "/"))
                if (!sourcePkgFile.exists()) throw IllegalArgumentException("source package $sourcePkg not found")

                sourcePkgFile.walk().forEach { file ->
                    val pkg = PkgConfig(sourceRoot, buildCopyDir, buildOutDir, file)
                    if (pkg != null && pkgs.none { it.dir == file }) {
                        pkgs.add(pkg)
                    }
                }
            }
             return pkgs
        }
    }
}

data class GradleConfig(
        val wrapper: File,
        val jar: File
) {

    companion object {

        operator fun invoke(projectDir: File, config: YamlGradleConfig?): GradleConfig {
            val wrapperPath = if (config?.wrapper == null) "gradlew" else config.wrapper
            val wrapper = projectDir.resolve(wrapperPath)
            if (!wrapper.exists()) throw IllegalArgumentException("gradle wrapper ${wrapper.relativeTo(projectDir)} not found")

            val jarPath = if (config?.jar == null) "build/libs/out.jar" else config.jar
            val jar = projectDir.resolve(jarPath)
            if (jar.extension != "jar") throw IllegalArgumentException("invalid gradle jar ${jar.relativeTo(projectDir)}")

            return GradleConfig(wrapper, jar)
        }
    }
}