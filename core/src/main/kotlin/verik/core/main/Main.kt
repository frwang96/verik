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

import verik.core.al.AlRuleParser
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.ItFile
import verik.core.it.reify.ItReifier
import verik.core.it.symbol.ItSymbolTableBuilder
import verik.core.kt.KtFile
import verik.core.kt.resolve.KtResolver
import verik.core.kt.symbol.KtSymbolTableBuilder
import verik.core.main.config.ProjectConfig
import verik.core.sv.build.SvSourceBuilder
import verik.core.vk.VkFile

const val VERSION = "1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)
    var gradleBuild = false

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
            StatusPrinter.info("cleaning header files", 1)
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
            for (pkg in projectConfig.symbolContext.pkgs()) {
                HeaderGenerator.generate(projectConfig, pkg)
            }
        }

        // gradle build
        if (mainArgs.contains(ExecutionType.GRADLE)) {
            if (!gradleBuild) {
                runGradle(projectConfig, "build")
                gradleBuild = true
            }
        }

        // compile files
        if (mainArgs.contains(ExecutionType.COMPILE)) {
            if (!gradleBuild) {
                runGradle(projectConfig, "build")
                gradleBuild = true
            }

            StatusPrinter.info("compiling files")
            StatusPrinter.info("copying files", 1)
            if (projectConfig.buildDir.exists()) {
                projectConfig.buildDir.deleteRecursively()
            }
            projectConfig.configFile.copyTo(projectConfig.configCopy)

            for (pkg in projectConfig.symbolContext.pkgs()) {
                val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg)
                pkgConfig.dir.listFiles()?.forEach {
                    it.copyTo(pkgConfig.copyDir.resolve(it.name))
                }
            }

            for (pkg in projectConfig.symbolContext.pkgs()) {
                val pkgConfig = projectConfig.symbolContext.pkgConfig(pkg)
                StatusPrinter.info("processing package ${pkgConfig.pkgKt}", 1)

                for (file in projectConfig.symbolContext.files(pkg)) {
                    val fileConfig = projectConfig.symbolContext.fileConfig(file)
                    StatusPrinter.info("+ ${fileConfig.file.relativeTo(projectConfig.projectDir)}", 2)
                    val out = compileFile(projectConfig, file)
                    fileConfig.outFile.parentFile.mkdirs()
                    fileConfig.outFile.writeText(out)
                }
            }

            StatusPrinter.info("generating compilation order ${projectConfig.orderFile.relativeTo(projectConfig.projectDir)}", 1)
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

private fun compileFile(projectConfig: ProjectConfig, file: Symbol): String {
    try {
        val fileConfig = projectConfig.symbolContext.fileConfig(file)
        val txtFile = fileConfig.copyFile.readText()

        // AL stage
        val alFile = AlRuleParser.parseKotlinFile(txtFile)

        // KT stage
        val ktFile = KtFile(alFile, file, projectConfig.symbolContext)
        val ktSymbolTable = KtSymbolTableBuilder.build(ktFile, projectConfig.symbolContext)
        KtResolver.resolve(ktFile, ktSymbolTable)

        // VK stage
        val vkFile = VkFile(ktFile)

        // IT stage
        val itFile = ItFile(vkFile)
        val itSymbolTable = ItSymbolTableBuilder.build(itFile)
        ItReifier.reify(itFile, itSymbolTable)

        // SV stage
        val svFile = itFile.extract(itSymbolTable)
        val lines = txtFile.count{ it == '\n' } + 1
        val labelLength = lines.toString().length
        val fileHeader = FileHeaderBuilder.build(projectConfig, fileConfig.file, fileConfig.outFile)
        val builder = SvSourceBuilder(projectConfig.compile.labelLines, labelLength, fileHeader)
        svFile.build(builder)

        return builder.toString()
    } catch (exception: LineException) {
        exception.file = file
        throw exception
    }
}
