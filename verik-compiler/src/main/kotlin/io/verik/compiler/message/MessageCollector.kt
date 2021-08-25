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

import io.verik.compiler.main.Config

class MessageCollector(
    private val config: Config,
    private val messagePrinter: MessagePrinter
) {

    private var errorCount = 0

    fun message(templateName: String, message: String, location: SourceLocation?, severity: Severity) {
        when (severity) {
            Severity.WARNING -> warning(templateName, message, location)
            Severity.ERROR -> error(templateName, message, location)
        }
    }

    fun flush() {
        if (errorCount != 0)
            throw MessageCollectorException()
    }

    private fun warning(templateName: String, message: String, location: SourceLocation?) {
        if (templateName in config.promotedWarnings)
            error(templateName, message, location)
        else if (templateName !in config.suppressedWarnings)
            messagePrinter.warning(templateName, message, location)
    }

    private fun error(templateName: String, message: String, location: SourceLocation?) {
        messagePrinter.error(templateName, message, location)
        incrementErrorCount()
    }

    private fun incrementErrorCount() {
        errorCount++
        if (errorCount >= config.maxErrorCount)
            throw MessageCollectorException()
    }

    companion object {

        lateinit var messageCollector: MessageCollector
    }
}
