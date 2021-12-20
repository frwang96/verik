/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.main

import io.verik.compiler.common.TextFile
import io.verik.compiler.message.GradleMessagePrinter
import io.verik.compiler.message.MessageCollector
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.serialize.general.FileHeaderBuilder
import java.nio.file.Files
import kotlin.io.path.exists

object VerikMain {

    fun run(config: VerikConfig, stageSequence: StageSequence) {
        if (config.buildDir.exists()) {
            config.buildDir.toFile().deleteRecursively()
        }

        val messagePrinter = GradleMessagePrinter(config)
        MessageCollector.messageCollector = MessageCollector(config, messagePrinter)
        val startTime = System.currentTimeMillis()
        try {
            val projectContext = ProjectContext(config)
            readFiles(projectContext)
            stageSequence.processAll(projectContext)
            writeFiles(projectContext)
        } catch (exception: Exception) {
            if (exception !is VerikException) {
                messagePrinter.error(
                    "Uncaught exception: ${exception.message}",
                    SourceLocation.NULL,
                    exception.stackTrace
                )
            }
            writeLogFile(config, messagePrinter, startTime, false)
            throw VerikException()
        }
        writeLogFile(config, messagePrinter, startTime, true)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.sourceSetContexts = projectContext.config.sourceSetConfigs.map { sourceSetConfig ->
            val textFiles = sourceSetConfig.files.map {
                val lines = Files.readAllLines(it)
                TextFile(it, lines.joinToString(separator = "\n", postfix = "\n"))
            }
            SourceSetContext(sourceSetConfig.name, textFiles)
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        MessageCollector.messageCollector.flush()
        val textFiles = projectContext.outputContext.getTextFiles()
        textFiles.forEach {
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }

    private fun writeLogFile(
        config: VerikConfig,
        messagePrinter: GradleMessagePrinter,
        startTime: Long,
        isPass: Boolean
    ) {
        val path = config.buildDir.resolve("log.txt")
        val fileHeader = FileHeaderBuilder.build(config, null, path, FileHeaderBuilder.HeaderStyle.TEXT)
        val messages = messagePrinter.toString()
        val elapsedTime = System.currentTimeMillis() - startTime
        val elapsedString = when {
            elapsedTime < 0 -> "0ms"
            elapsedTime < 10000 -> "${elapsedTime}ms"
            else -> "${elapsedTime / 1000}s"
        }

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.append(messages)
        if (isPass)
            builder.appendLine("i: Compile passed in $elapsedString")
        else
            builder.appendLine("i: Compile failed in $elapsedString")

        Files.createDirectories(path.parent)
        Files.writeString(path, builder)
    }
}
