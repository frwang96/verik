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
        val gradle: ProjectGradleConfig,
        val source: ProjectSourceConfig,
        val compile: ProjectCompileConfig,
        val stubs: ProjectStubsConfig?
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

            val gradle = ProjectGradleConfig(projectDir, config.gradleDir)
            val source = ProjectSourceConfig(configFile, projectDir, buildCopyDir, buildOutDir, config.src)
            val compile = ProjectCompileConfig(config.compile)
            val stubs = ProjectStubsConfig(projectDir, config.stubs)

            return ProjectConfig(
                    timeString,
                    configFile,
                    projectDir,
                    config.project,
                    buildDir,
                    buildCopyDir,
                    buildOutDir,
                    gradle,
                    source,
                    compile,
                    stubs
            )
        }
    }
}

data class ProjectGradleConfig(
        val dir: File,
        val wrapperSh: File,
        val wrapperBat: File
) {

    companion object {

        operator fun invoke(projectDir: File, gradleDir: String?): ProjectGradleConfig {
            val dir = projectDir.resolve(gradleDir ?: ".")
            if (!dir.exists()) throw IllegalArgumentException("gradle directory ${dir.relativeTo(projectDir)} not found")

            val wrapperSh = dir.resolve("gradlew")
            if (!wrapperSh.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

            val wrapperBat = dir.resolve("gradlew.bat")
            if (!wrapperBat.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

            return ProjectGradleConfig(dir, wrapperSh, wrapperBat)
        }
    }
}

data class ProjectSourceConfig(
        val root: File,
        val pkgs: List<PkgConfig>
) {

    companion object {

        operator fun invoke(
                configFile: File,
                projectDir: File,
                buildCopyDir: File,
                buildOutDir: File,
                config: YamlSourceConfig?
        ): ProjectSourceConfig {
            val root = projectDir.resolve(config?.root ?: "src/main/kotlin")
            if (!root.exists()) {
                throw IllegalArgumentException("source root ${root.relativeTo(projectDir)} not found")
            }

            val pkgStrings = config?.pkgs ?: listOf("")
            val pkgs = ArrayList<PkgConfig>()

            for (pkgString in pkgStrings) {
                if (pkgStrings.any { it.startsWith("$pkgString.") }) {
                    StatusPrinter.warning("redundant package $pkgString in ${configFile.relativeTo(projectDir)}")
                }
            }

            for (pkgString in pkgStrings) {
                val pkgFile = root.resolve(pkgString.replace(".", "/"))
                if (!pkgFile.exists()) throw IllegalArgumentException("package $pkgString not found")

                pkgFile.walk().forEach { file ->
                    val pkg = PkgConfig(root, buildCopyDir, buildOutDir, file)
                    if (pkg != null && pkgs.none { it.dir == file }) {
                        pkgs.add(pkg)
                    }
                }
            }

            return ProjectSourceConfig(root, pkgs)
        }
    }
}

data class ProjectCompileConfig(
        val top: String?,
        val scopeType: CompileScopeType,
        val labelLines: Boolean
) {

    companion object {

        operator fun invoke(
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
    }
}

data class ProjectStubsConfig(
        val main: String,
        val jar: File
) {

    companion object {

        operator fun invoke(projectDir: File, config: YamlStubsConfig?): ProjectStubsConfig? {
            return if (config?.main != null) {
                val jarPath = (config.jar) ?: "build/libs/out.jar"
                val jar = projectDir.resolve(jarPath)
                if (jar.extension != "jar") throw IllegalArgumentException("invalid jar ${jar.relativeTo(projectDir)}")
                ProjectStubsConfig(config.main, jar)
            } else null
        }
    }
}
