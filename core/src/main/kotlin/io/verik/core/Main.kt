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
import java.io.File

const val VERSION = "1.0"

fun main(args: Array<String>) {
    val startTime = System.nanoTime()
    val mainArgs = MainArgs(args)

    StatusPrinter.info("loading project configuration ${mainArgs.configPath}")
    val config = try {
        ProjectConfig(mainArgs.configPath)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    StatusPrinter.info("copying source files")
    try {
        if (config.buildDir.exists()) {
            config.buildDir.deleteRecursively()
        }
        val configFile = File(mainArgs.configPath)
        configFile.copyTo(config.configCopy)

        for (pkg in config.pkgs) {
            pkg.dir.listFiles()?.forEach {
                it.copyTo(pkg.copyDir.resolve(it.name))
            }
        }
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    for (pkg in config.pkgs) {
        for (source in pkg.sources) {
            StatusPrinter.info("processing ${source.source.relativeTo(config.projectDir)}")
            try {
                val sourceString = getSourceString(config, source)
                source.out.parentFile.mkdirs()
                source.out.writeText(sourceString)
            } catch (exception: Exception) {
                StatusPrinter.error(exception.message, exception)
            }
        }
    }


    StatusPrinter.info("generating ${config.orderFile.relativeTo(config.projectDir)}")
    try {
        val sourceList = OrderFileBuilder.build(config)
        config.orderFile.writeText(sourceList)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val endTime = System.nanoTime()
    StatusPrinter.info("execution successful in ${(endTime - startTime + 999999999) / 1000000000}s")
}

private fun getSourceString(config: ProjectConfig, source: SourceConfig): String {
    val txtFile = try {
        source.copy.readText()
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val ktFile = try {
        KtRuleParser.parseKotlinFile(txtFile)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val vkFile = try {
        VkFile(ktFile)
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    val svFile = try {
        vkFile.extract()
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }

    return try {
        val lines = txtFile.count{ it == '\n' } + 1
        val labelLength = lines.toString().length
        val builder = SourceBuilder(config, source, labelLength)
        svFile.build(builder)
        builder.toString()
    } catch (exception: Exception) {
        StatusPrinter.error(exception.message, exception)
    }
}
