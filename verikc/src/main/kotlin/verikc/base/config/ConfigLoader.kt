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

package verikc.base.config

import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
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
        val buildCacheDir = buildDir.resolve("cache")
        val buildOutDir = buildDir.resolve("out")

        val symbolContext = SymbolContext()
        val gradle = loadProjectGradleConfig(projectDir, projectYaml.gradleDir)
        val compile = loadProjectCompileConfig(projectYaml.compile)
        val rconf = loadProjectRconfConfig(projectDir, projectYaml.rconf)
        val compilationUnit = loadCompilationUnitConfig(
            configFile,
            projectDir,
            buildCopyDir,
            buildOutDir,
            projectYaml.srcDir,
            projectYaml.compile,
            symbolContext
        )

        val pkgCount = compilationUnit.pkgCount()
        val fileCount = compilationUnit.fileCount()
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
            buildCacheDir,
            buildOutDir,
            symbolContext,
            gradle,
            compile,
            rconf,
            compilationUnit
        )
    }

    private fun loadProjectGradleConfig(projectDir: File, gradleDir: String?): ProjectGradleConfig {
        val dir = projectDir.resolve(gradleDir ?: ".")
        if (!dir.exists()) throw IllegalArgumentException("gradle directory ${dir.relativeTo(projectDir)} not found")

        val wrapperSh = dir.resolve("gradlew")
        if (!wrapperSh.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

        val wrapperBat = dir.resolve("gradlew.bat")
        if (!wrapperBat.exists()) throw IllegalArgumentException("gradle wrapper ${wrapperSh.relativeTo(projectDir)} not found")

        return ProjectGradleConfig(dir, wrapperSh, wrapperBat)
    }

    private fun loadProjectCompileConfig(compileYaml: ProjectCompileYaml?): ProjectCompileConfig {
        return if (compileYaml != null) {
            ProjectCompileConfig(
                compileYaml.top,
                compileYaml.labelLines ?: true
            )
        } else ProjectCompileConfig(
            null,
            true
        )
    }

    private fun loadProjectRconfConfig(projectDir: File, rconfYaml: ProjectRconfYaml?): ProjectRconfConfig? {
        return if (rconfYaml?.main != null) {
            val jarPath = (rconfYaml.jar) ?: "build/libs/out.jar"
            val jar = projectDir.resolve(jarPath)
            if (jar.extension != "jar") throw IllegalArgumentException("invalid jar ${jar.relativeTo(projectDir)}")
            ProjectRconfConfig(rconfYaml.main, jar)
        } else null
    }

    private fun loadCompilationUnitConfig(
        configFile: File,
        projectDir: File,
        buildCopyDir: File,
        buildOutDir: File,
        srcDir: String?,
        compileYaml: ProjectCompileYaml?,
        symbolContext: SymbolContext
    ): CompilationUnitConfig {
        val sourceRoot = projectDir.resolve(srcDir ?: "src/main/kotlin")
        if (!sourceRoot.exists()) {
            throw IllegalArgumentException("source root ${sourceRoot.relativeTo(projectDir)} not found")
        }

        val basePkgStrings = compileYaml?.pkgs ?: listOf("")
        for (basePkgString in basePkgStrings) {
            if (basePkgStrings.any { it.startsWith("$basePkgString.") }) {
                StatusPrinter.warning("redundant package $basePkgString in ${configFile.relativeTo(projectDir)}")
            }
        }

        val pkgDirSet = HashSet<File>()
        for (basePkgString in basePkgStrings) {
            val basePkgDir = sourceRoot.resolve(basePkgString.replace(".", "/"))
            if (!basePkgDir.exists()) throw IllegalArgumentException("package $basePkgString not found")

            basePkgDir.walk().forEach { dir ->
                if (!pkgDirSet.contains(dir) && dir.isDirectory && getPkgFiles(dir).isNotEmpty()) {
                    if (dir == sourceRoot) throw IllegalArgumentException("use of the root package is prohibited")
                    pkgDirSet.add(dir)
                }
            }
        }

        val pkgDirs = pkgDirSet.sorted()
        val pkgConfigs = ArrayList<PkgConfig>()
        for (pkgDir in pkgDirs) {
            pkgConfigs.add(loadPkgConfig(sourceRoot, buildCopyDir, buildOutDir, pkgDir, symbolContext))
        }

        return CompilationUnitConfig(pkgConfigs)
    }

    private fun loadPkgConfig(
        sourceRoot: File,
        buildCopyDir: File,
        buildOutDir: File,
        dir: File,
        symbolContext: SymbolContext
    ): PkgConfig {
        val relativePath = dir.relativeTo(sourceRoot)
        val identifierKt = relativePath.toString().replace("/", ".")
        val identifierSv = identifierKt.replace(".", "_") + "_pkg"
        val copyDir = buildCopyDir.resolve(relativePath)
        val outDir = buildOutDir.resolve(relativePath)
        val symbol = symbolContext.registerSymbol(identifierKt)
        val fileConfigs = getPkgFiles(dir).map {
            loadFileConfig(sourceRoot, buildCopyDir, buildOutDir, it, symbol, symbolContext)
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
        sourceRoot: File,
        buildCopyDir: File,
        buildOutDir: File,
        file: File,
        pkgSymbol: Symbol,
        symbolContext: SymbolContext
    ): FileConfig {
        val relativePath = file.relativeTo(sourceRoot)
        val identifier = relativePath.path
        val copyFile = buildCopyDir.resolve(relativePath)
        val outDir = buildOutDir.resolve(relativePath).parentFile
        val outModuleFile = outDir.resolve("${file.nameWithoutExtension}.sv")
        val outPkgFile = outDir.resolve("${file.nameWithoutExtension}.svh")
        val symbol = symbolContext.registerSymbol(identifier)

        return FileConfig(
            identifier,
            file,
            copyFile,
            outModuleFile,
            outPkgFile,
            symbol,
            pkgSymbol
        )
    }

    private fun getPkgFiles(pkgDir: File): List<File> {
        val pkgFiles = pkgDir
            .listFiles()
            ?.filter { it.extension == "kt" && it.name != "headers.kt" }
            ?.sorted()
        return pkgFiles ?: listOf()
    }
}
