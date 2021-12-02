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

    fun warning(templateName: String, message: String, location: SourceLocation) {
        if (templateName in config.promotedWarnings)
            fatal(message, location)
        else if (templateName !in config.suppressedWarnings)
            messagePrinter.warning(message, location)
    }

    fun fatal(message: String, location: SourceLocation): Nothing {
        messagePrinter.error(message, location)
        throw VerikImporterException()
    }

    companion object {

        lateinit var messageCollector: MessageCollector
    }
}
