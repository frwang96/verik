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

package io.verik.importer.message

import io.verik.importer.main.VerikImporterConfig
import io.verik.importer.main.VerikImporterException

class MessageCollector(
    private val config: VerikImporterConfig,
    private val messagePrinter: MessagePrinter
) {

    private var errorCount = 0

    fun flush() {
        if (errorCount != 0)
            throw VerikImporterException()
    }

    fun warning(templateName: String, message: String, location: SourceLocation) {
        if (templateName in config.promotedWarnings)
            fatal(message, location)
        else if (templateName !in config.suppressedWarnings)
            messagePrinter.warning(message, location, Thread.currentThread().stackTrace)
    }

    fun error(message: String, location: SourceLocation) {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        incrementErrorCount()
    }

    fun fatal(message: String, location: SourceLocation): Nothing {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        throw VerikImporterException()
    }

    private fun incrementErrorCount() {
        errorCount++
        if (errorCount >= config.maxErrorCount)
            throw VerikImporterException()
    }

    companion object {

        lateinit var messageCollector: MessageCollector
    }
}
