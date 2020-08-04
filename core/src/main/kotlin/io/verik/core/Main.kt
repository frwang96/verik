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

import io.verik.core.config.ProjectConfig
import io.verik.core.config.SourceConfig
import io.verik.core.kt.KtRuleParser
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
            for (pkg in config.pkgs) {
                if (pkg.header.exists()) {
                    StatusPrinter.info("- ${pkg.header.relativeTo(config.projectDir)}", 2)
                    pkg.header.delete()
                }
            }
            runGradle(config, "clean")
        }

        // generate source headers
        if (mainArgs.contains(ExecutionType.HEADERS)) {
            StatusPrinter.info("generating header files")
            for (pkg in config.pkgs) {
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

        // compile sources
        if (mainArgs.contains(ExecutionType.COMPILE)) {
            if (!gradleBuild) {
                runGradle(config, "build")
                gradleBuild = true
            }

            StatusPrinter.info("compiling source files")
            StatusPrinter.info("copying source files", 1)
            if (config.buildDir.exists()) {
                config.buildDir.deleteRecursively()
            }
            config.configFile.copyTo(config.configCopy)

            for (pkg in config.pkgs) {
                pkg.dir.listFiles()?.forEach {
                    it.copyTo(pkg.copyDir.resolve(it.name))
                }
            }

            for (pkg in config.pkgs) {
                if (pkg.pkgNameKt == "") {
                    StatusPrinter.info("processing package", 1)
                } else {
                    StatusPrinter.info("processing package ${pkg.pkgNameKt}", 1)
                }

                for (source in pkg.sources) {
                    StatusPrinter.info("+ ${source.source.relativeTo(config.projectDir)}", 2)
                    val sourceString = getSourceString(config, source)
                    source.out.parentFile.mkdirs()
                    source.out.writeText(sourceString)
                }
            }

            StatusPrinter.info("generating compilation order file ${config.orderFile.relativeTo(config.projectDir)}", 1)
            val sourceList = OrderFileBuilder.build(config)
            config.orderFile.writeText(sourceList)
        }

        // generate test stubs
        if (mainArgs.contains(ExecutionType.STUBS)) {
            if (config.stubsMain != null) {
                if (!gradleBuild) {
                    runGradle(config, "build")
                }

                StatusPrinter.info("generating test stubs")
                val processArgs = listOf("java", "-cp", config.gradle.jar.absolutePath, config.stubsMain, config.stubsFile.absolutePath)
                val process = ProcessBuilder(processArgs).inheritIO().start()
                process.waitFor()
                if (process.exitValue() != 0) {
                    throw RuntimeException("test stub generation failed")
                }
            }
        }
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val endTime = System.nanoTime()
    StatusPrinter.info("execution successful in ${(endTime - startTime + 999999999) / 1000000000}s")
    println()
}

private fun runGradle(config: ProjectConfig, task: String) {
    StatusPrinter.info("running gradle $task")
    val args = listOf(config.gradle.wrapper.absolutePath, "-p", config.gradle.wrapper.parentFile.absolutePath, task)
    val process = ProcessBuilder(args).inheritIO().start()
    process.waitFor()
    if (process.exitValue() != 0) {
        throw RuntimeException("gradle $task failed")
    }
}

private fun getSourceString(config: ProjectConfig, source: SourceConfig): String {
    val txtFile = source.copy.readText()
    val ktFile = KtRuleParser.parseKotlinFile(source.source.name, txtFile)
    val vkFile = VkFile(ktFile)
    val svFile = vkFile.extract()

    val lines = txtFile.count{ it == '\n' } + 1
    val labelLength = lines.toString().length
    val fileHeader = FileHeaderBuilder.build(config, source.source, source.out)
    val builder = SourceBuilder(config.labelLines, labelLength, fileHeader)
    svFile.build(builder)
    return builder.toString()
}
