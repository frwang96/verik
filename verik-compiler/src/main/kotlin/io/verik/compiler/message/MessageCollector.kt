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
import io.verik.compiler.common.location
import org.jetbrains.kotlin.psi.KtElement

open class MessageCollector {

    var errorCount = 0

    open fun fatal(message: String, location: SourceLocation?): Nothing {
        throw MessageCollectorException()
    }

    open fun error(message: String, location: SourceLocation?) {
        errorCount++
    }

    open fun warning(message: String, location: SourceLocation?) {}

    open fun log(message: String) {}

    open fun flush() {}

    fun fatal(message: String, element: EElement): Nothing {
        fatal(message, element.location)
    }

    fun fatal(message: String, element: KtElement): Nothing {
        fatal(message, element.location())
    }

    fun error(message: String, element: EElement) {
        error(message, element.location)
    }

    fun error(message: String, element: KtElement) {
        error(message, element.location())
    }

    fun warning(message: String, element: EElement) {
        warning(message, element.location)
    }
}
