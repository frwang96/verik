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

class GradleMessagePrinter(private val debug: Boolean) : MessagePrinter() {

    override fun warning(message: String, location: SourceLocation) {
        print("w: ")
        printMessage(message, location)
        if (debug)
            printStackTrace(Thread.currentThread().stackTrace)
    }

    override fun error(message: String, location: SourceLocation) {
        print("e: ")
        printMessage(message, location)
        if (debug)
            printStackTrace(Thread.currentThread().stackTrace)
    }

    private fun printMessage(message: String, location: SourceLocation) {
        print("${location.path}: ")
        if (location.line != 0)
            print("(${location.line}, ${location.column}): ")
        println(message)
    }

    companion object {

        private const val STACK_TRACE_SIZE_MAX = 16
        private const val STACK_TRACE_SIZE_TRUNCATED = 12

        fun printStackTrace(stackTrace: Array<StackTraceElement>) {
            val firstIndex = stackTrace.indexOfLast { it.className.contains("MessageTemplate") } + 1
            val lastIndex = stackTrace.indexOfFirst { it.className == "io.verik.importer.main.VerikImporterMain" } + 1
            val truncatedList = stackTrace.toList().subList(firstIndex, lastIndex)
            if (truncatedList.size < STACK_TRACE_SIZE_MAX) {
                truncatedList.forEach { println("    at $it") }
            } else {
                truncatedList
                    .take(STACK_TRACE_SIZE_TRUNCATED)
                    .forEach { println("    at $it") }
                println("    ... ${truncatedList.size - STACK_TRACE_SIZE_TRUNCATED} more ...")
            }
        }
    }
}
