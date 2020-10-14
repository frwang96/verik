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

package verik.core.main.config

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import verik.core.base.SymbolContext
import verik.core.main.StatusPrinter
import verik.core.yaml.ProjectCompileYaml
import verik.core.yaml.ProjectSourceYaml
import verik.core.yaml.ProjectStubsYaml
import verik.core.yaml.ProjectYaml
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConfigLoader {

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

        val projectYaml = Yaml(Constructor(ProjectYaml::class.java)).load<ProjectYaml>(configFile.readText())

        val project = projectYaml.project
                ?: throw IllegalArgumentException("project name expected in ${configFile.relativeTo(projectDir)}")

        val buildDir = projectDir.resolve(projectYaml.buildDir ?: "build/verik")
        val buildCopyDir = buildDir.resolve("src")
        val buildOutDir = buildDir.resolve("out")

        val gradle = loadProjectGradleConfig(projectDir, projectYaml.gradleDir)
        val compile = loadProjectCompileConfig(projectYaml.compile)
        val stubs = loadProjectStubsConig(projectDir, projectYaml.stubs)
        val symbolContext = loadSymbolContext(configFile, projectDir, buildCopyDir, buildOutDir, projectYaml.src)

        val pkgCount = symbolContext.countPkgs()
        val fileCount = symbolContext.countFiles()
        val pkgString = if (pkgCount == 1) "package" else "packages"
        val fileString = if (fileCount == 1) "file" else "files"
        StatusPrinter.info("found $pkgCount $pkgString $fileCount $fileString", 1)

        return ProjectConfig(
                timeString,
                configFile,
                projectDir,
                project,
                buildDir,
                buildCopyDir,
                buildOutDir,
                gradle,
                compile,
                stubs,
                symbolContext
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
            compileYaml: ProjectCompileYaml?
    ): ProjectCompileConfig {
        return if (compileYaml != null) {
            ProjectCompileConfig(
                    compileYaml.top,
                    CompileScopeType(compileYaml.scope),
                    compileYaml.labelLines ?: true
            )
        } else ProjectCompileConfig(
                null,
                CompileScopeType(null),
                true
        )
    }

    private fun loadProjectStubsConig(
            projectDir: File,
            stubsYaml: ProjectStubsYaml?
    ): ProjectStubsConfig? {
        return if (stubsYaml?.main != null) {
            val jarPath = (stubsYaml.jar) ?: "build/libs/out.jar"
            val jar = projectDir.resolve(jarPath)
            if (jar.extension != "jar") throw IllegalArgumentException("invalid jar ${jar.relativeTo(projectDir)}")
            ProjectStubsConfig(stubsYaml.main, jar)
        } else null
    }

    private fun loadSymbolContext(
            configFile: File,
            projectDir: File,
            buildCopyDir: File,
            buildOutDir: File,
            sourceYaml: ProjectSourceYaml?
    ): SymbolContext {
        val root = projectDir.resolve(sourceYaml?.root ?: "src/main/kotlin")
        if (!root.exists()) {
            throw IllegalArgumentException("source root ${root.relativeTo(projectDir)} not found")
        }

        val pkgStrings = sourceYaml?.pkgs ?: listOf("")
        for (pkgString in pkgStrings) {
            if (pkgStrings.any { it.startsWith("$pkgString.") }) {
                StatusPrinter.warning("redundant package $pkgString in ${configFile.relativeTo(projectDir)}")
            }
        }

        val symbolContext = SymbolContext()
        val pkgSet = HashSet<String>()
        for (pkgString in pkgStrings) {
            val pkgFile = root.resolve(pkgString.replace(".", "/"))
            if (!pkgFile.exists()) throw IllegalArgumentException("package $pkgString not found")

            pkgFile.walk().forEach { file ->
                val pkgAndFileConfigs = loadPkgAndFileConfigs(root, buildCopyDir, buildOutDir, file)
                if (pkgAndFileConfigs != null) {
                    val pkgName = pkgAndFileConfigs.first.pkgKt
                    if (!pkgSet.contains(pkgName)) {
                        symbolContext.registerConfigs(pkgAndFileConfigs.first, pkgAndFileConfigs.second)
                        pkgSet.add(pkgName)
                    }
                }
            }
        }

        return symbolContext
    }

    private fun loadPkgAndFileConfigs(
            sourceRoot: File,
            buildCopyDir: File,
            buildOutDir: File,
            dir: File
    ): Pair<PkgConfig, List<FileConfig>>? {
        val relativePath = dir.relativeTo(sourceRoot)
        val pkgKt = relativePath.toString().replace("/", ".")
        val pkgSv = pkgKt.replace(".", "_") + "_pkg"
        val copyDir = buildCopyDir.resolve(relativePath)
        val outDir = buildOutDir.resolve(relativePath)
        val pkgWrapperFile = outDir.resolve("$pkgSv.sv")
        return if (dir.isDirectory) {
            val files = dir.listFiles()?.apply { sort() }?.filter { it.extension == "kt" && it.name != "headers.kt" }
            if (files != null && files.isNotEmpty()) {

                val configFiles = files.map { loadFileConfig(
                        sourceRoot,
                        buildCopyDir,
                        buildOutDir,
                        it
                ) }
                val configPkg = PkgConfig(
                        dir,
                        copyDir,
                        outDir,
                        pkgKt,
                        pkgSv,
                        pkgWrapperFile
                )
                Pair(configPkg, configFiles)
            } else null
        } else null
    }

    private fun loadFileConfig(
            sourceRoot: File,
            buildCopyDir: File,
            buildOutDir: File,
            file: File
    ): FileConfig {
        val relativePath = file.relativeTo(sourceRoot)
        val copyFile = buildCopyDir.resolve(relativePath)
        val parent = buildOutDir.resolve(relativePath).parentFile
        val nameModule = "${file.nameWithoutExtension}.sv"
        val outFileModule = parent.resolve(nameModule)
        val namePkg = "${file.nameWithoutExtension}.svh"
        val outFilePkg = parent.resolve(namePkg)
        return FileConfig(
                file,
                copyFile,
                outFileModule,
                outFilePkg
        )
    }
}