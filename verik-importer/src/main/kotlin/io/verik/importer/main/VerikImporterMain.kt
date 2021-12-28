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

package io.verik.importer.main

import io.verik.importer.common.TextFile
import io.verik.importer.message.GradleMessagePrinter
import io.verik.importer.message.MessageCollector
import io.verik.importer.message.SourceLocation
import io.verik.importer.serialize.general.FileHeaderBuilder
import java.nio.file.Files
import kotlin.io.path.exists

object VerikImporterMain {

    fun run(config: VerikImporterConfig, stageSequence: StageSequence) {
        if (config.buildDir.exists())
            config.buildDir.toFile().deleteRecursively()
        if (config.importedFiles.isEmpty())
            return

        val messagePrinter = GradleMessagePrinter(config)
        MessageCollector.messageCollector = MessageCollector(config, messagePrinter)
        val startTime = System.currentTimeMillis()
        try {
            val importerContext = ImporterContext(config)
            readFiles(importerContext)
            stageSequence.process(importerContext)
            writeFiles(importerContext)
        } catch (exception: Exception) {
            if (exception !is VerikImporterException) {
                messagePrinter.error(
                    "Uncaught exception: ${exception.message}",
                    SourceLocation.NULL,
                    exception.stackTrace
                )
            }
            writeLogFile(config, messagePrinter, startTime, false)
            throw VerikImporterException()
        }
        writeLogFile(config, messagePrinter, startTime, true)
    }

    private fun readFiles(importerContext: ImporterContext) {
        importerContext.inputFileContexts = importerContext.config.importedFiles.map {
            val lines = Files.readAllLines(it)
            val textFile = TextFile(it, lines.joinToString(separator = "\n", postfix = "\n"))
            InputFileContext(textFile)
        }
    }

    private fun writeFiles(importerContext: ImporterContext) {
        val textFiles = importerContext.outputContext.getTextFiles()
        textFiles.forEach {
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }

    private fun writeLogFile(
        config: VerikImporterConfig,
        messagePrinter: GradleMessagePrinter,
        startTime: Long,
        isPass: Boolean
    ) {
        val path = config.buildDir.resolve("log.txt")
        val fileHeader = FileHeaderBuilder.build(config, path, FileHeaderBuilder.HeaderStyle.TEXT)
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
            builder.appendLine("i: Import passed in $elapsedString")
        else
            builder.appendLine("i: Import failed in $elapsedString")

        Files.createDirectories(path.parent)
        Files.writeString(path, builder)
    }
}
