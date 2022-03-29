/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

import io.verik.compiler.main.VerikConfig

/**
 * Message printer that prints gradle style error and warning messages.
 */
class GradleMessagePrinter(config: VerikConfig) : MessagePrinter() {

    private val debug = config.debug
    private val builder = StringBuilder()

    private val STACK_TRACE_SIZE_MAX = 16
    private val STACK_TRACE_SIZE_TRUNCATED = 12

    override fun warning(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        logPrint("w: ")
        printMessage(message, location)
        if (debug) {
            printStackTrace(stackTrace)
        }
    }

    override fun error(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        logPrint("e: ")
        printMessage(message, location)
        if (debug) {
            printStackTrace(stackTrace)
        }
    }

    override fun toString(): String {
        return builder.toString()
    }

    private fun printMessage(message: String, location: SourceLocation) {
        if (location != SourceLocation.NULL) {
            logPrint("${location.path.toAbsolutePath()}: ")
            if (location.line != 0) {
                logPrint("(${location.line}, ${location.column}): ")
            }
        } else {
            if (!message.contains(":")) {
                logPrint("Unknown error: ")
            }
        }
        logPrintLine(message)
    }

    private fun printStackTrace(stackTrace: Array<StackTraceElement>) {
        val firstIndex = stackTrace.indexOfLast { it.className.contains("MessageTemplate") } + 1
        val lastIndex = stackTrace.indexOfFirst { it.className == "io.verik.compiler.main.VerikMain" } + 1
        val truncatedList = stackTrace.toList().subList(firstIndex, lastIndex)
        if (truncatedList.size < STACK_TRACE_SIZE_MAX) {
            truncatedList.forEach { logPrintLine("    at $it") }
        } else {
            truncatedList
                .take(STACK_TRACE_SIZE_TRUNCATED)
                .forEach { logPrintLine("    at $it") }
            logPrintLine("    ... ${truncatedList.size - STACK_TRACE_SIZE_TRUNCATED} more ...")
        }
    }

    private fun logPrint(content: String) {
        print(content)
        builder.append(content)
    }

    private fun logPrintLine(content: String) {
        println(content)
        builder.appendLine(content)
    }
}
