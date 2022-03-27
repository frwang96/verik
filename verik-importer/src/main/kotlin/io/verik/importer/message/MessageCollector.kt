/*
 * SPDX-License-Identifier: Apache-2.0
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
