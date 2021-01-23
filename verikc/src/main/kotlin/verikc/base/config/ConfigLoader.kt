/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.base.config

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.lang.util.LangIdentifierUtil
import verikc.main.OSType
import verikc.main.OSUtil
import verikc.main.StatusPrinter
import verikc.yaml.ProjectCompileYaml
import verikc.yaml.ProjectRconfYaml
import verikc.yaml.ProjectYaml
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ConfigLoader {

    fun loadProjectConfig(configPath: String): ProjectConfig {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        val timeString = current.format(formatter)

        if (OSUtil.getOSType() == OSType.OTHER) {
            throw IllegalArgumentException("operating system not supported")
        }

        val configFile = File(configPath).absoluteFile
        if (!configFile.exists()) {
            throw IllegalArgumentException("project configuration $configPath not found")
        }
        val projectDir = configFile.parentFile

        StatusPrinter.info("loading project configuration ${configFile.relativeTo(projectDir)}")

        val yaml = Yaml(Constructor(ProjectYaml::class.java)).load<ProjectYaml>(configFile.readText())

        val project = yaml.project
            ?: throw IllegalArgumentException("project name expected in ${configFile.relativeTo(projectDir)}")

        val symbolContext = SymbolContext()
        val pathConfig = loadProjectPathConfig(configFile, projectDir, yaml)
        val compileConfig = loadProjectCompileConfig(pathConfig, yaml.compile)
        val rconfConfig = loadProjectRconfConfig(pathConfig, yaml.rconf)
        val compilationUnitConfig = loadCompilationUnitConfig(pathConfig, compileConfig, symbolContext)

        val pkgCount = compilationUnitConfig.pkgCount()
        val fileCount = compilationUnitConfig.fileCount()
        val pkgString = if (pkgCount == 1) "package" else "packages"
        val fileString = if (fileCount == 1) "file" else "files"
        StatusPrinter.info("found $pkgCount $pkgString $fileCount $fileString", 1)

        return ProjectConfig(
            timeString,
            project,
            pathConfig,
            compileConfig,
            rconfConfig,
            compilationUnitConfig,
            symbolContext
        )
    }

    private fun loadProjectPathConfig(configFile: File, projectDir: File, yaml: ProjectYaml): ProjectPathConfig {
        val srcDir = projectDir.resolve(yaml.srcDir ?: "src/main/kotlin")
        val gradleDir = projectDir.resolve(yaml.gradleDir ?: ".")
        val buildDir = projectDir.resolve(yaml.buildDir ?: "build/verik")

        if (!srcDir.exists())
            throw IllegalArgumentException("source directory ${srcDir.relativeTo(projectDir)} not found")
        if (!gradleDir.exists())
            throw IllegalArgumentException("gradle directory ${srcDir.relativeTo(projectDir)} not found")

        val pathConfig = ProjectPathConfig(
            configFile,
            projectDir,
            srcDir,
            gradleDir,
            buildDir
        )

        if (!pathConfig.gradleWrapperSh.exists())
            throw IllegalArgumentException("gradle wrapper ${pathConfig.gradleWrapperSh.relativeTo(projectDir)} not found")
        if (!pathConfig.gradleWrapperBat.exists())
            throw IllegalArgumentException("gradle wrapper ${pathConfig.gradleWrapperBat.relativeTo(projectDir)} not found")

        return pathConfig
    }

    private fun loadProjectCompileConfig(
        pathConfig: ProjectPathConfig,
        compileYaml: ProjectCompileYaml?
    ): ProjectCompileConfig {
        return if (compileYaml != null) {
            val topIdentifier = compileYaml.top
                ?: throw IllegalArgumentException("top module expected")
            try {
                LangIdentifierUtil.checkIdentifier(topIdentifier, Line(0))
            } catch (exception: LineException) {
                throw IllegalArgumentException("illegal identifier for top")
            }

            val basePkgIdentifiers = compileYaml.pkgs ?: listOf("")
            for (basePkgIdentifier in basePkgIdentifiers) {
                if (basePkgIdentifiers.any { it.startsWith("$basePkgIdentifier.") }) {
                    StatusPrinter.warning("redundant package $basePkgIdentifier in ${
                        pathConfig.configFile.relativeTo(pathConfig.projectDir)
                    }")
                }
            }
            val labelLines = compileYaml.labelLines ?: true

            ProjectCompileConfig(
                topIdentifier,
                basePkgIdentifiers,
                labelLines
            )
        } else throw IllegalArgumentException("compile configuration expected")
    }

    private fun loadProjectRconfConfig(
        pathConfig: ProjectPathConfig,
        rconfYaml: ProjectRconfYaml?
    ): ProjectRconfConfig? {
        return if (rconfYaml?.main != null) {
            val jarPath = (rconfYaml.jar) ?: "build/libs/out.jar"
            val jarFile = pathConfig.projectDir.resolve(jarPath)
            if (jarFile.extension != "jar")
                throw IllegalArgumentException("invalid jar ${jarFile.relativeTo(pathConfig.projectDir)}")
            ProjectRconfConfig(rconfYaml.main, jarFile)
        } else null
    }

    private fun loadCompilationUnitConfig(
        pathConfig: ProjectPathConfig,
        compileConfig: ProjectCompileConfig,
        symbolContext: SymbolContext
    ): CompilationUnitConfig {
        val pkgDirSet = HashSet<File>()
        for (basePkgIdentifier in compileConfig.basePkgIdentifiers) {
            val basePkgDir = pathConfig.srcDir.resolve(basePkgIdentifier.replace(".", "/"))
            if (!basePkgDir.exists()) throw IllegalArgumentException("package $basePkgIdentifier not found")

            basePkgDir.walk().forEach {
                if (!pkgDirSet.contains(it) && it.isDirectory && getPkgFiles(it).isNotEmpty()) {
                    pkgDirSet.add(it)
                }
            }
        }

        val pkgDirs = pkgDirSet.sorted()
        val pkgConfigs = ArrayList<PkgConfig>()
        for (pkgDir in pkgDirs) {
            pkgConfigs.add(loadPkgConfig(pathConfig, pkgDir, symbolContext))
        }

        return CompilationUnitConfig(pkgConfigs)
    }

    private fun loadPkgConfig(pathConfig: ProjectPathConfig, dir: File, symbolContext: SymbolContext): PkgConfig {
        val relativePath = dir.relativeTo(pathConfig.srcDir)
        val identifierKt = relativePath.toString().replace("/", ".")
        val identifierSv = if (identifierKt == "") "pkg" else identifierKt.replace(".", "_") + "_pkg"
        val copyDir = pathConfig.copyDir.resolve(relativePath)
        val outDir = pathConfig.outDir.resolve(relativePath)
        val symbol = symbolContext.registerSymbol(identifierKt)
        val fileConfigs = getPkgFiles(dir).map {
            loadFileConfig(pathConfig, it, symbol, symbolContext)
        }

        return PkgConfig(
            identifierKt,
            identifierSv,
            dir,
            copyDir,
            outDir,
            symbol,
            fileConfigs
        )
    }

    private fun loadFileConfig(
        pathConfig: ProjectPathConfig,
        file: File,
        pkgSymbol: Symbol,
        symbolContext: SymbolContext
    ): FileConfig {
        val relativePath = file.relativeTo(pathConfig.srcDir)
        val identifier = relativePath.path

        val copyFile = pathConfig.copyDir.resolve(relativePath)
        val cacheDir = pathConfig.cacheDir.resolve(relativePath).parentFile
        val cacheFile = cacheDir.resolve("${file.nameWithoutExtension}.txt")
        val outDir = pathConfig.outDir.resolve(relativePath).parentFile
        val outComponentFile = outDir.resolve("${file.nameWithoutExtension}.sv")
        val outPkgFile = outDir.resolve("${file.nameWithoutExtension}.svh")
        val symbol = symbolContext.registerSymbol(identifier)

        return FileConfig(
            identifier,
            file,
            copyFile,
            cacheFile,
            outComponentFile,
            outPkgFile,
            symbol,
            pkgSymbol
        )
    }

    private fun getPkgFiles(pkgDir: File): List<File> {
        val pkgFiles = pkgDir
            .listFiles()
            ?.filter { it.extension == "kt" && it.name != "Headers.kt" }
            ?.sorted()
        return pkgFiles ?: listOf()
    }
}
