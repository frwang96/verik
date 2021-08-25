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

import io.verik.compiler.ast.element.common.EElement

class MessageTemplate0(
    override val severity: Severity,
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: EElement) {
        MessageCollector.messageCollector.message(name, format(), element.location, severity)
    }
}

class MessageTemplate1<A>(
    override val severity: Severity,
    override val template: String
) : AbstractMessageTemplate() {

    fun on(location: SourceLocation?, a: A) {
        MessageCollector.messageCollector.message(name, format(a), location, severity)
    }

    fun on(element: EElement, a: A) {
        MessageCollector.messageCollector.message(name, format(a), element.location, severity)
    }
}

class MessageTemplate2<A, B>(
    override val severity: Severity,
    override val template: String
) : AbstractMessageTemplate() {

    fun on(element: EElement, a: A, b: B) {
        MessageCollector.messageCollector.message(name, format(a, b), element.location, severity)
    }
}
