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

package verikc.main

import verikc.kt.KtDriver
import verikc.kt.ast.KtCompilationUnit
import verikc.main.config.ProjectConfig
import verikc.ps.PsDriver
import verikc.rf.RfDriver
import verikc.vk.VkDriver
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

const val VERSION = "0.1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)

    var gradleBuild = false
    var ktCompilationUnit: KtCompilationUnit? = null

    StatusPrinter.info("VERIKC $VERSION")

    try {
        val projectConfig = ProjectConfig(mainArgs.configPath)
        StatusPrinter.setSymbolContext(projectConfig.symbolContext)

        // clean build output
        if (mainArgs.contains(ExecutionType.CLEAN)) {
            StatusPrinter.info("cleaning build output")
            if (projectConfig.buildDir.exists()) {
                StatusPrinter.info("cleaning build directory", 1)
                projectConfig.buildDir.deleteRecursively()
            }
            StatusPrinter.info("cleaning headers", 1)
            for (pkg in projectConfig.symbolContext.pkgs()) {
                val header = projectConfig.symbolContext.pkgConfig(pkg).header
                if (header.exists()) {
                    StatusPrinter.info("- ${header.relativeTo(projectConfig.projectDir)}", 2)
                    header.delete()
                }
            }
            runGradle(projectConfig, "clean")
        }

        // generate headers
        if (mainArgs.contains(ExecutionType.HEADERS)) {
            StatusPrinter.info("generating headers")
            copyFiles(projectConfig)
            ktCompilationUnit = KtDriver.parse(projectConfig)
            HeaderGenerator.generate(projectConfig, ktCompilationUnit)
        }

        // gradle build
        if (mainArgs.contains(ExecutionType.GRADLE)) {
            if (!gradleBuild) {
                runGradle(projectConfig, "build")
                gradleBuild = true
            }
        }

        // run compilation
        if (mainArgs.contains(ExecutionType.COMPILE)) {
            if (!gradleBuild) {
                runGradle(projectConfig, "build")
                gradleBuild = true
            }

            StatusPrinter.info("running compilation")

            // prepare output directory
            if (ktCompilationUnit == null) {
                copyFiles(projectConfig)
            }
            if (projectConfig.buildOutDir.exists()) {
                projectConfig.buildOutDir.deleteRecursively()
            }
            if (projectConfig.orderFile.exists()) {
                projectConfig.orderFile.delete()
            }

            // drive main stages
            ktCompilationUnit = ktCompilationUnit ?: KtDriver.parse(projectConfig)
            KtDriver.drive(projectConfig, ktCompilationUnit)
            val vkCompilationUnit = VkDriver.drive(ktCompilationUnit)
            val rfCompilationUnit = RfDriver.drive(projectConfig, vkCompilationUnit)
            PsDriver.drive(projectConfig, rfCompilationUnit)
        }

        // generate test stubs
        if (mainArgs.contains(ExecutionType.STUBS)) {
            if (projectConfig.stubs != null) {
                if (!gradleBuild) {
                    runGradle(projectConfig, "build")
                }

                StatusPrinter.info("running test stub generation")
                if (projectConfig.stubsFile.exists()) {
                    projectConfig.stubsFile.delete()
                }

                val processArgs = listOf(
                        "java",
                        "-cp",
                        projectConfig.stubs.jar.absolutePath,
                        projectConfig.stubs.main,
                        projectConfig.projectDir.absolutePath,
                        projectConfig.stubsFile.absolutePath
                )
                val process = ProcessBuilder(processArgs).start()
                val stdout = BufferedReader(InputStreamReader(process.inputStream))
                val stderr = BufferedReader(InputStreamReader(process.errorStream))

                val builder = StringBuilder()
                var line = stdout.readLine()
                while (line != null) {
                    builder.appendLine(line)
                    line = stdout.readLine()
                }
                process.waitFor()
                if (process.exitValue() != 0) {
                    line = stderr.readLine()
                    if (line != null) StatusPrinter.errorMessage(line)
                    line = stderr.readLine()
                    while (line != null) {
                        println(line)
                        line = stderr.readLine()
                    }
                    println()
                    exitProcess(1)
                }
                projectConfig.stubsFile.parentFile.mkdirs()
                projectConfig.stubsFile.writeText(builder.toString())
            }
        }
    } catch (exception: Exception) {
        StatusPrinter.error(exception)
    }

    val endTime = System.nanoTime()
    StatusPrinter.info("execution successful in ${(endTime - startTime + 999999999) / 1000000000}s")
    println()
}

private fun copyFiles(projectConfig: ProjectConfig) {
    if (projectConfig.configCopy.exists()) {
        projectConfig.configCopy.delete()
    }
    if (projectConfig.buildCopyDir.exists()) {
        projectConfig.buildCopyDir.deleteRecursively()
    }

    projectConfig.configFile.copyTo(projectConfig.configCopy)
    for (pkg in projectConfig.symbolContext.pkgs()) {
        val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg)
        pkgConfig.dir.listFiles()?.forEach {
            it.copyTo(pkgConfig.copyDir.resolve(it.name))
        }
    }

}

private fun runGradle(projectConfig: ProjectConfig, task: String) {
    StatusPrinter.info("running gradle $task")
    val args = listOf(
            projectConfig.gradle.wrapperSh.absolutePath,
            "-p",
            projectConfig.gradle.dir.absolutePath,
            task
    )
    val process = ProcessBuilder(args).inheritIO().start()
    process.waitFor()
    if (process.exitValue() != 0) {
        throw RuntimeException("gradle $task failed")
    }
}
