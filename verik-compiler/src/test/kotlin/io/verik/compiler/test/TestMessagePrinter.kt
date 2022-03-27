/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.test

import io.verik.compiler.message.MessagePrinter
import io.verik.compiler.message.SourceLocation

class TestMessagePrinter : MessagePrinter() {

    override fun warning(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        throw TestWarningException(message)
    }

    override fun error(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>) {
        throw TestErrorException(message)
    }
}
