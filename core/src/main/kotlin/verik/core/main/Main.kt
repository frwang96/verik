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

package verik.core.main

import verik.core.rf.drive.RfDriver
import verik.core.kt.KtCompilationUnit
import verik.core.kt.drive.KtDriver
import verik.core.main.config.ProjectConfig
import verik.core.vk.drive.VkDriver

const val VERSION = "0.1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)

    var gradleBuild = false
    var ktCompilationUnit: KtCompilationUnit? = null

    StatusPrinter.info("VERIK $VERSION")

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

            // clean output directory
            if (projectConfig.buildDir.exists()) {
                projectConfig.buildDir.deleteRecursively()
            }

            // copy files
            projectConfig.configFile.copyTo(projectConfig.configCopy)
            for (pkg in projectConfig.symbolContext.pkgs()) {
                val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg)
                pkgConfig.dir.listFiles()?.forEach {
                    it.copyTo(pkgConfig.copyDir.resolve(it.name))
                }
            }

            // drive main stages
            ktCompilationUnit = ktCompilationUnit ?: KtDriver.parse(projectConfig)
            KtDriver.drive(projectConfig, ktCompilationUnit)
            val vkCompilationUnit = VkDriver.drive(ktCompilationUnit)
            RfDriver.drive(projectConfig, vkCompilationUnit)

            // write order file
            StatusPrinter.info("+ ${projectConfig.orderFile.relativeTo(projectConfig.projectDir)}", 2)
            val order = OrderFileBuilder.build(projectConfig)
            projectConfig.orderFile.writeText(order)
        }

        // generate test stubs
        if (mainArgs.contains(ExecutionType.STUBS)) {
            if (projectConfig.stubs != null) {
                if (!gradleBuild) {
                    runGradle(projectConfig, "build")
                }

                StatusPrinter.info("running test stub generation")
                val processArgs = listOf(
                        "java",
                        "-cp",
                        projectConfig.stubs.jar.absolutePath,
                        projectConfig.stubs.main,
                        projectConfig.projectDir.absolutePath,
                        projectConfig.stubsFile.absolutePath
                )
                val process = ProcessBuilder(processArgs).inheritIO().start()
                process.waitFor()
                if (process.exitValue() != 0) {
                    throw RuntimeException("test stub generation failed")
                }
            }
        }
    } catch (exception: Exception) {
        StatusPrinter.error(exception)
    }

    val endTime = System.nanoTime()
    StatusPrinter.info("execution successful in ${(endTime - startTime + 999999999) / 1000000000}s")
    println()
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
