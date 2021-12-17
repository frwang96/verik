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

package io.verik.compiler.message

import io.verik.compiler.main.VerikConfig

class GradleMessagePrinter(config: VerikConfig) : MessagePrinter() {

    private val debug = config.debug
    private val builder = StringBuilder()

    private val STACK_TRACE_SIZE_MAX = 16
    private val STACK_TRACE_SIZE_TRUNCATED = 12

    override fun warning(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        logPrint("w: ")
        printMessage(message, location)
        if (debug)
            printStackTrace(stackTrace)
    }

    override fun error(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        logPrint("e: ")
        printMessage(message, location)
        if (debug)
            printStackTrace(stackTrace)
    }

    override fun toString(): String {
        return builder.toString()
    }

    private fun printMessage(message: String, location: SourceLocation) {
        if (location != SourceLocation.NULL) {
            logPrint("${location.path.toAbsolutePath()}: ")
            if (location.line != 0)
                logPrint("(${location.line}, ${location.column}): ")
        } else {
            if (!message.contains(":"))
                logPrint("Unknown error: ")
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
