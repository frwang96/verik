/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

abstract class MessagePrinter {

    abstract fun warning(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>)

    abstract fun error(message: String, location: SourceLocation, stackTrace: Array<StackTraceElement>)
}
