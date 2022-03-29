/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.message

import io.verik.importer.ast.element.common.EElement
import org.antlr.v4.runtime.tree.TerminalNode

/**
 * Warning message template with one argument.
 */
class WarningMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A) {
        MessageCollector.messageCollector.warning(name, format(a), location)
    }

    fun on(element: EElement, a: A) {
        MessageCollector.messageCollector.warning(name, format(a), element.location)
    }
}

/**
 * Error message template with no arguments.
 */
class ErrorMessageTemplate0(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(terminalNode: TerminalNode) {
        MessageCollector.messageCollector.error(format(), SourceLocation.get(terminalNode))
    }
}

/**
 * Error message template with one argument.
 */
class ErrorMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A) {
        MessageCollector.messageCollector.error(format(a), location)
    }

    fun on(terminalNode: TerminalNode, a: A) {
        MessageCollector.messageCollector.error(format(a), SourceLocation.get(terminalNode))
    }
}

/**
 * Error message template with two arguments.
 */
class ErrorMessageTemplate2<A, B>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A, b: B) {
        MessageCollector.messageCollector.error(format(a, b), location)
    }
}

/**
 * Fatal message template with one argument.
 */
class FatalMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), location)
    }

    fun on(element: EElement, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), element.location)
    }
}

/**
 * Fatal message template with two arguments.
 */
class FatalMessageTemplate2<A, B>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: EElement, a: A, b: B): Nothing {
        MessageCollector.messageCollector.fatal(format(a, b), element.location)
    }
}
