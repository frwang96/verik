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

class GradleMessagePrinter(private val debug: Boolean) : MessagePrinter() {

    override fun warning(templateName: String, message: String, location: SourceLocation?) {
        print("w: ")
        printMessage(templateName, message, location)
        if (debug)
            printStackTrace()
    }

    override fun error(templateName: String, message: String, location: SourceLocation?) {
        print("e: ")
        printMessage(templateName, message, location)
        if (debug)
            printStackTrace()
        throw MessageCollectorException()
    }

    private fun printMessage(templateName: String, message: String, location: SourceLocation?) {
        if (location != null)
            print("${location.path}: (${location.line}, ${location.column}): ")
        if (debug)
            print("[$templateName] ")
        println(message)
    }

    private fun printStackTrace() {
        Thread.currentThread().stackTrace.forEach { stackTraceElement ->
            if (stackTraceElement.className.startsWith("io.verik") &&
                listOf("MessagePrinter", "MessageTemplate").none { stackTraceElement.className.contains(it) }
            )
                println("    at $stackTraceElement")
        }
    }
}
