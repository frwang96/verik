/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.main

import io.verik.compiler.message.GradleMessagePrinter
import io.verik.compiler.message.MessageCollector
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.serialize.general.FileHeaderBuilder
import java.nio.file.Files
import kotlin.io.path.exists

/**
 * Entry point of the compiler. The compiler reads the input files, processes the stage sequence, and writes the output
 * files. Messages are written to a log file.
 */
object VerikCompilerMain {

    fun run(config: VerikCompilerConfig) {
        if (config.buildDir.exists()) {
            config.buildDir.toFile().deleteRecursively()
        }

        val messagePrinter = GradleMessagePrinter(config)
        MessageCollector.messageCollector = MessageCollector(config, messagePrinter)
        val startTime = System.currentTimeMillis()
        try {
            val projectContext = ProjectContext(config)
            readFiles(projectContext)
            StageSequencer.getStageSequence().processAll(projectContext)
            writeFiles(projectContext)
        } catch (exception: Exception) {
            if (exception !is VerikCompilerException) {
                messagePrinter.error(
                    "Uncaught exception: ${exception.message}",
                    SourceLocation.NULL,
                    exception.stackTrace
                )
            }
            writeLogFile(config, messagePrinter, startTime, false)
            throw VerikCompilerException()
        }
        writeLogFile(config, messagePrinter, startTime, true)
    }

    private fun readFiles(projectContext: ProjectContext) {
        projectContext.sourceSetContexts = projectContext.config.sourceSetConfigs.map { sourceSetConfig ->
            val textFiles = sourceSetConfig.files.map { Platform.readTextFile(it) }
            SourceSetContext(sourceSetConfig.name, textFiles)
        }
    }

    private fun writeFiles(projectContext: ProjectContext) {
        MessageCollector.messageCollector.flush()
        val textFiles = projectContext.outputContext.getTextFiles()
        textFiles.forEach { Platform.writeTextFile(it) }
    }

    private fun writeLogFile(
        config: VerikCompilerConfig,
        messagePrinter: GradleMessagePrinter,
        startTime: Long,
        isPass: Boolean
    ) {
        val path = config.buildDir.resolve("log.txt")
        val fileHeader = FileHeaderBuilder.build(config, null, path, FileHeaderBuilder.CommentStyle.HASH)
        val messages = messagePrinter.toString()
        val elapsedTime = System.currentTimeMillis() - startTime
        val elapsedString = when {
            elapsedTime < 0 -> "0ms"
            elapsedTime < 10000 -> "${elapsedTime}ms"
            else -> "${elapsedTime / 1000}s"
        }

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine()
        builder.append(messages)
        if (isPass) {
            builder.appendLine("i: Compile passed in $elapsedString")
        } else {
            builder.appendLine("i: Compile failed in $elapsedString")
        }

        Files.createDirectories(path.parent)
        Files.writeString(path, builder)
    }
}
