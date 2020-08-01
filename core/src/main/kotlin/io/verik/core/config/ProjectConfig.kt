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

data class ProjectConfig(
        val projectDir: File,
        val project: String,
        val pkgs: List<PkgConfig>,
        val buildDir: File,
        val buildSourceDir: File,
        val top: String?,
        val labelLines: Boolean,
        val gradle: GradleConfig,
        val stubsMain: String?
) {

    val sourceListFile = buildDir.resolve("srcs.txt")

    companion object {

        operator fun invoke(configPath: String): ProjectConfig {
            val configFile = File(configPath).absoluteFile
            val projectDir = configFile.parentFile
            if (!configFile.exists()) throw IllegalArgumentException("project configuration $configPath not found")

            val config = Yaml.default.parse(YamlProjectConfig.serializer(), configFile.readText())

            val sourceRoot = projectDir.resolve(config.srcRoot ?: "src/main/kotlin")
            if (!sourceRoot.exists()) throw IllegalArgumentException("source root ${sourceRoot.relativeTo(projectDir)} not found")

            val buildDir = projectDir.resolve(config.buildDir ?: "build")
            val buildSourceDir = buildDir.resolve("src")

            val pkgs = getPkgs(configPath, sourceRoot, buildSourceDir, config.srcPkgs ?: listOf(""))

            val labelLines = config.labelLines ?: true
            val gradle = GradleConfig(projectDir, config.gradle)

            return ProjectConfig(projectDir, config.project, pkgs, buildDir, buildSourceDir,
                    config.top, labelLines, gradle, config.stubsMain)
        }

        private fun getPkgs(configPath: String, sourceRoot: File, buildSourceDir: File, sourcePkgs: List<String>): List<PkgConfig> {
            val pkgs = ArrayList<PkgConfig>()

            for (sourcePkg in sourcePkgs) {
                if (sourcePkgs.any { it.startsWith("$sourcePkg.") }) {
                    StatusPrinter.warning("redundant source package $sourcePkg in $configPath")
                }
            }

            for (sourcePkg in sourcePkgs) {
                val sourcePkgFile = sourceRoot.resolve(sourcePkg.replace(".", "/"))
                if (!sourcePkgFile.exists()) throw IllegalArgumentException("source package $sourcePkg not found")

                sourcePkgFile.walk().forEach { file ->
                    val pkg = PkgConfig(sourceRoot, buildSourceDir, file)
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
            if (!jar.exists()) throw IllegalArgumentException("gradle jar ${wrapper.relativeTo(projectDir)} not found")
            if (jar.extension != "jar") throw IllegalArgumentException("invalid gradle jar ${jar.relativeTo(projectDir)}")

            return GradleConfig(wrapper, jar)
        }
    }
}