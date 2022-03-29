/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.message

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.common.location
import org.jetbrains.kotlin.psi.KtElement

/**
 * Warning message template with no arguments.
 */
class WarningMessageTemplate0(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: KtElement) {
        MessageCollector.messageCollector.warning(name, format(), element.location())
    }
}

/**
 * Warning message template with one argument.
 */
class WarningMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A) {
        MessageCollector.messageCollector.warning(name, format(a), location)
    }
}

/**
 * Error message template with no arguments.
 */
class ErrorMessageTemplate0(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: KtElement) {
        MessageCollector.messageCollector.error(format(), element.location())
    }

    fun on(element: EElement) {
        MessageCollector.messageCollector.error(format(), element.location)
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

    fun on(element: KtElement, a: A) {
        MessageCollector.messageCollector.error(format(a), element.location())
    }

    fun on(element: EElement, a: A) {
        MessageCollector.messageCollector.error(format(a), element.location)
    }
}

/**
 * Error message template with two arguments.
 */
class ErrorMessageTemplate2<A, B>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: EElement, a: A, b: B) {
        MessageCollector.messageCollector.error(format(a, b), element.location)
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

    fun on(element: KtElement, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), element.location())
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
