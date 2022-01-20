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

import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.sv.element.common.SvElement
import org.antlr.v4.runtime.tree.TerminalNode

class WarningMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A) {
        MessageCollector.messageCollector.warning(name, format(a), location)
    }

    fun on(element: SvElement, a: A) {
        MessageCollector.messageCollector.warning(name, format(a), element.location)
    }
}

class ErrorMessageTemplate0(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(terminalNode: TerminalNode) {
        MessageCollector.messageCollector.error(format(), SourceLocation.get(terminalNode))
    }
}

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

class ErrorMessageTemplate2<A, B>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A, b: B) {
        MessageCollector.messageCollector.error(format(a, b), location)
    }
}

class FatalMessageTemplate1<A>(
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), location)
    }

    fun on(element: SvElement, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), element.location)
    }

    fun on(element: KtElement, a: A): Nothing {
        MessageCollector.messageCollector.fatal(format(a), element.location)
    }
}
