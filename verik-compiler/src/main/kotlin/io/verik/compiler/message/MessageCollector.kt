/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

import io.verik.compiler.main.VerikCompilerConfig
import io.verik.compiler.main.VerikCompilerException

/**
 * Message collector that dispatches messages to a [messagePrinter]. It terminates the compiler when the error count
 * exceeds the maximum error count or if it receives a fatal message.
 */
class MessageCollector(
    private val config: VerikCompilerConfig,
    private val messagePrinter: MessagePrinter
) {

    private var errorCount = 0

    fun flush() {
        if (errorCount != 0)
            throw VerikCompilerException()
    }

    fun warning(templateName: String, message: String, location: SourceLocation) {
        if (templateName in config.promotedWarnings) {
            error(message, location)
        } else if (templateName !in config.suppressedWarnings) {
            messagePrinter.warning(message, location, Thread.currentThread().stackTrace)
        }
    }

    fun error(message: String, location: SourceLocation) {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        incrementErrorCount()
    }

    fun fatal(message: String, location: SourceLocation): Nothing {
        messagePrinter.error(message, location, Thread.currentThread().stackTrace)
        throw VerikCompilerException()
    }

    private fun incrementErrorCount() {
        errorCount++
        if (errorCount >= config.maxErrorCount) throw VerikCompilerException()
    }

    companion object {

        lateinit var messageCollector: MessageCollector
    }
}
