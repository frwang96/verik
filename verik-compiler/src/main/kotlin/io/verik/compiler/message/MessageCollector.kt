/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

import io.verik.compiler.main.VerikConfig
import io.verik.compiler.main.VerikException

class MessageCollector(
    private val config: VerikConfig,
    private val messagePrinter: MessagePrinter
) {

    private var errorCount = 0

    fun flush() {
        if (errorCount != 0)
            throw VerikException()
    }

    fun warning(templateName: String, message: String, location: SourceLocation) {
        if (templateName in config.promotedWarnings)
            error(message, location)
        else if (templateName !in config.suppressedWarnings)
            messagePrinter.warning(message, location, Thread.currentThread().stackTrace)
    }

    fun error(message: String, location: SourceLocation) {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        incrementErrorCount()
    }

    fun fatal(message: String, location: SourceLocation): Nothing {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        throw VerikException()
    }

    private fun incrementErrorCount() {
        errorCount++
        if (errorCount >= config.maxErrorCount)
            throw VerikException()
    }

    companion object {

        lateinit var messageCollector: MessageCollector
    }
}
