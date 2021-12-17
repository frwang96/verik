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

import io.verik.importer.message.GradleMessagePrinter
import io.verik.importer.message.MessageCollector
import io.verik.importer.message.SourceLocation
import java.nio.file.Files
import kotlin.io.path.exists

object VerikImporterMain {

    fun run(config: VerikImporterConfig, stageSequence: StageSequence) {
        if (config.importedFiles.isEmpty()) {
            if (config.buildDir.exists())
                config.buildDir.toFile().deleteRecursively()
            return
        }

        val messagePrinter = GradleMessagePrinter(config)
        MessageCollector.messageCollector = MessageCollector(config, messagePrinter)
        try {
            val importerContext = ImporterContextBuilder.buildContext(config)
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
            throw VerikImporterException()
        }
    }

    private fun writeFiles(importerContext: ImporterContext) {
        if (Files.exists(importerContext.config.buildDir)) {
            importerContext.config.buildDir.toFile().deleteRecursively()
        }
        val textFiles = importerContext.outputContext.getTextFiles()
        textFiles.forEach {
            Files.createDirectories(it.path.parent)
            Files.writeString(it.path, it.content)
        }
    }
}
