/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.main

import io.verik.importer.message.GradleMessagePrinter
import io.verik.importer.message.MessageCollector
import io.verik.importer.message.SourceLocation
import io.verik.importer.serialize.general.FileHeaderBuilder
import java.nio.file.Files
import kotlin.io.path.exists

/**
 * Entry point of the importer. The importer reads the input files, processes the stage sequence, and writes the output
 * files. Messages are written to a log file.
 */
object VerikImporterMain {

    fun run(config: VerikImporterConfig, stageSequence: StageSequence) {
        if (config.buildDir.exists())
            config.buildDir.toFile().deleteRecursively()
        if (config.importedFiles.isEmpty())
            return

        val messagePrinter = GradleMessagePrinter(config)
        MessageCollector.messageCollector = MessageCollector(config, messagePrinter)
        val startTime = System.currentTimeMillis()
        val projectContext = ProjectContext(config)
        try {
            readFiles(projectContext)
            stageSequence.processAll(projectContext)
            writeFiles(projectContext)
        } catch (exception: Exception) {
            if (exception !is VerikImporterException) {
                messagePrinter.error(
                    "Uncaught exception: ${exception.message}",
                    SourceLocation.NULL,
                    exception.stackTrace
                )
            }
            writeLogFile(config, messagePrinter, startTime, false)
            val preprocessorTextFile = projectContext.outputContext.preprocessorTextFile
            if (preprocessorTextFile != null) {
                Platform.writeTextFile(preprocessorTextFile)
            }
            throw VerikImporterException()
        }
        writeLogFile(config, messagePrinter, startTime, true)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.inputFileContexts = projectContext.config.importedFiles.map {
            val textFile = Platform.readTextFile(it)
            InputFileContext(textFile)
        }
        projectContext.includedTextFiles = IncludedTextFileReader.read(
            projectContext.inputFileContexts,
            projectContext.config.includeDirs
        )
    }

    private fun writeFiles(projectContext: ProjectContext) {
        MessageCollector.messageCollector.flush()
        val textFiles = projectContext.outputContext.getTextFiles()
        textFiles.forEach { Platform.writeTextFile(it) }
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
