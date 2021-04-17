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

package verikc.main

import verikc.al.AlStageDriver
import verikc.base.config.ProjectConfig
import verikc.kt.KtStageDriver
import verikc.ps.PsStageDriver
import verikc.rs.RsStageDriver
import verikc.sv.SvStageDriver
import verikc.tx.TxStageDriver
import verikc.vk.VkStageDriver
import java.io.BufferedReader
import java.io.InputStreamReader

const val VERSION = "0.1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)

    StatusPrinter.info("VERIKC $VERSION")

    try {
        val projectConfig = loadProjectConfig(mainArgs)

        // clean build output
        if (mainArgs.contains(ExecutionType.CLEAN)) {
            StatusPrinter.info("cleaning build output")
            if (projectConfig.pathConfig.buildDir.exists()) {
                StatusPrinter.info("cleaning build directory", 1)
                projectConfig.pathConfig.buildDir.deleteRecursively()
            }
            StatusPrinter.info("cleaning headers", 1)
            for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
                val header = pkgConfig.header
                if (header.exists()) {
                    StatusPrinter.info("- ${header.relativeTo(projectConfig.pathConfig.projectDir)}", 2)
                    header.delete()
                }
            }
            runGradle(projectConfig, "clean")
        }

        // gradle build
        if (mainArgs.contains(ExecutionType.GRADLE)) {
            runGradle(projectConfig, "build")
        }

        // run compilation
        if (mainArgs.contains(ExecutionType.COMPILE)) {
            StatusPrinter.info("running compilation")

            // prepare output directory
            copyFiles(projectConfig)
            if (projectConfig.pathConfig.outDir.exists()) {
                projectConfig.pathConfig.outDir.deleteRecursively()
            }
            if (projectConfig.pathConfig.orderFile.exists()) {
                projectConfig.pathConfig.orderFile.delete()
            }

            // drive main stages
            var stageTime = System.nanoTime()
            val alCompilationUnit = AlStageDriver.parse(projectConfig)
            StatusPrinter.info("completed stage al in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val ktCompilationUnit = KtStageDriver.parse(
                alCompilationUnit,
                projectConfig.compileConfig.topIdentifier,
                projectConfig.symbolContext
            )
            StatusPrinter.info("completed stage kt in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val rsCompilationUnit = RsStageDriver.build(ktCompilationUnit)
            RsStageDriver.resolve(rsCompilationUnit)
            StatusPrinter.info("completed stage rs in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val vkCompilationUnit = VkStageDriver.build(rsCompilationUnit)
            StatusPrinter.info("completed stage vk in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val psCompilationUnit = PsStageDriver.build(vkCompilationUnit)
            PsStageDriver.pass(psCompilationUnit)
            StatusPrinter.info("completed stage ps in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val svCompilationUnit = SvStageDriver.extract(psCompilationUnit)
            StatusPrinter.info("completed stage sv in ${getElapsedString(stageTime)}", 1)

            stageTime = System.nanoTime()
            val txCompilationUnit = TxStageDriver.build(svCompilationUnit, projectConfig)
            StatusPrinter.info("completed stage tx in ${getElapsedString(stageTime)}", 1)

            TxStageDriver.write(txCompilationUnit, projectConfig)
        }
    } catch (exception: Exception) {
        StatusPrinter.error(exception)
    }

    StatusPrinter.info("execution successful in ${getElapsedString(startTime)}")
    println()
}

private fun loadProjectConfig(mainArgs: MainArgs): ProjectConfig {
    val projectConfig = ProjectConfig(mainArgs.configPath)
    StatusPrinter.setSymbolContext(projectConfig.symbolContext)

    val configPath = projectConfig.pathConfig.configFile.relativeTo(projectConfig.pathConfig.projectDir)
    val pkgCount = projectConfig.compilationUnitConfig.pkgCount()
    val fileCount = projectConfig.compilationUnitConfig.fileCount()
    val pkgString = if (pkgCount == 1) "package" else "packages"
    val fileString = if (fileCount == 1) "file" else "files"
    StatusPrinter.info("loading project configuration $configPath")
    StatusPrinter.info("found $pkgCount $pkgString $fileCount $fileString", 1)

    return projectConfig
}

private fun copyFiles(projectConfig: ProjectConfig) {
    if (projectConfig.pathConfig.configCopyFile.exists()) {
        projectConfig.pathConfig.configCopyFile.delete()
    }
    if (projectConfig.pathConfig.copyDir.exists()) {
        projectConfig.pathConfig.copyDir.deleteRecursively()
    }

    projectConfig.pathConfig.configFile.copyTo(projectConfig.pathConfig.configCopyFile)
    for (pkgConfig in projectConfig.compilationUnitConfig.pkgConfigs) {
        pkgConfig.dir.listFiles()?.forEach {
            it.copyTo(pkgConfig.copyDir.resolve(it.name))
        }
    }

}

private fun runGradle(projectConfig: ProjectConfig, task: String) {
    StatusPrinter.info("running gradle $task")
    val args = listOf(
        "gradle",
        "-p",
        projectConfig.pathConfig.gradleDir.absolutePath,
        task,
        "--console=plain"
    )

    val process = ProcessBuilder(args).start()
    val stdout = BufferedReader(InputStreamReader(process.inputStream))
    val stderr = BufferedReader(InputStreamReader(process.errorStream))

    var line = stdout.readLine()
    while (line != null) {
        if (line.isNotEmpty() && !line.startsWith("BUILD") && !line.startsWith("> Task :")) {
            StatusPrinter.info(line, 1)
        }
        line = stdout.readLine()
    }
    process.waitFor()
    line = stderr.readLine()
    while (line != null) {
        if (line.startsWith("e: ")) StatusPrinter.info(line.substring(3), 1)
        line = stderr.readLine()
    }
    if (process.exitValue() != 0) {
        throw RuntimeException("gradle $task failed")
    }
}

private fun getElapsedString(startTime: Long): String {
    val endTime = System.nanoTime()
    val elapsed = (endTime - startTime + 999999) / 1000000
    return "${elapsed / 1000}.${(elapsed % 1000).toString().padStart(3, '0')}s"
}
