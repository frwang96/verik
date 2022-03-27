/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.test

import io.verik.importer.message.MessagePrinter
import io.verik.importer.message.SourceLocation

class TestMessagePrinter : MessagePrinter() {

    override fun warning(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        throw TestWarningException(message)
    }

    override fun error(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        throw TestErrorException(message)
    }
}
