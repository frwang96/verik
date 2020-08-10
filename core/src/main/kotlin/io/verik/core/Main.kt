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

package io.verik.core

import io.verik.core.al.AlRuleParser
import io.verik.core.config.ProjectConfig
import io.verik.core.symbol.FileTableFile
import io.verik.core.vk.VkFile

const val VERSION = "1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)
    var gradleBuild = false

    try {
        val config = ProjectConfig(mainArgs.configPath)

        // clean build output
        if (mainArgs.contains(ExecutionType.CLEAN)) {
            StatusPrinter.info("cleaning build output")
            if (config.buildDir.exists()) {
                StatusPrinter.info("cleaning build directory", 1)
                config.buildDir.deleteRecursively()
            }
            StatusPrinter.info("cleaning header files", 1)
            for (pkg in config.fileTable.pkgs) {
                if (pkg.config.header.exists()) {
                    StatusPrinter.info("- ${pkg.config.header.relativeTo(config.projectDir)}", 2)
                    pkg.config.header.delete()
                }
            }
            runGradle(config, "clean")
        }

        // generate headers
        if (mainArgs.contains(ExecutionType.HEADERS)) {
            StatusPrinter.info("generating headers")
            for (pkg in config.fileTable.pkgs) {
                HeaderGenerator.generate(config, pkg)
            }
        }

        // gradle build
        if (mainArgs.contains(ExecutionType.GRADLE)) {
            if (!gradleBuild) {
                runGradle(config, "build")
                gradleBuild = true
            }
        }

        // compile files
        if (mainArgs.contains(ExecutionType.COMPILE)) {
            if (!gradleBuild) {
                runGradle(config, "build")
                gradleBuild = true
            }

            StatusPrinter.info("compiling files")
            StatusPrinter.info("copying files", 1)
            if (config.buildDir.exists()) {
                config.buildDir.deleteRecursively()
            }
            config.configFile.copyTo(config.configCopy)

            for (pkg in config.fileTable.pkgs) {
                pkg.config.dir.listFiles()?.forEach {
                    it.copyTo(pkg.config.copyDir.resolve(it.name))
                }
            }

            for (pkg in config.fileTable.pkgs) {
                StatusPrinter.info("processing package ${pkg.config.pkgString}", 1)

                for (file in pkg.files) {
                    StatusPrinter.info("+ ${file.config.file.relativeTo(config.projectDir)}", 2)
                    val out = compileFile(config, file)
                    file.config.outFile.parentFile.mkdirs()
                    file.config.outFile.writeText(out)
                }
            }

            StatusPrinter.info("generating compilation order ${config.orderFile.relativeTo(config.projectDir)}", 1)
            val order = OrderFileBuilder.build(config)
            config.orderFile.writeText(order)
        }

        // generate test stubs
        if (mainArgs.contains(ExecutionType.STUBS)) {
            if (config.stubs != null) {
                if (!gradleBuild) {
                    runGradle(config, "build")
                }

                StatusPrinter.info("running test stub generation")
                val processArgs = listOf(
                        "java",
                        "-cp",
                        config.stubs.jar.absolutePath,
                        config.stubs.main,
                        config.projectDir.absolutePath,
                        config.stubsFile.absolutePath
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

private fun runGradle(config: ProjectConfig, task: String) {
    StatusPrinter.info("running gradle $task")
    val args = listOf(config.gradle.wrapperSh.absolutePath, "-p", config.gradle.dir.absolutePath, task)
    val process = ProcessBuilder(args).inheritIO().start()
    process.waitFor()
    if (process.exitValue() != 0) {
        throw RuntimeException("gradle $task failed")
    }
}

private fun compileFile(config: ProjectConfig, file: FileTableFile): String {
    try {
        val txtFile = file.config.copyFile.readText()
        val alFile = AlRuleParser.parseKotlinFile(txtFile)
        val vkFile = VkFile(alFile)
        val svFile = vkFile.extract()

        val lines = txtFile.count{ it == '\n' } + 1
        val labelLength = lines.toString().length
        val fileHeader = FileHeaderBuilder.build(config, file.config.file, file.config.outFile)
        val builder = SourceBuilder(config.compile.labelLines, labelLength, fileHeader)
        svFile.build(builder)
        return builder.toString()
    } catch (exception: LineException) {
        throw SourceLineException(exception.message, exception.line, file.config.file)
    }
}
