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

package io.verik.importer.ast.element

import io.verik.importer.ast.common.Visitor
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    inline fun <reified E : EElement> cast(): E? {
        return when (this) {
            is E -> this
            else -> {
                Messages.INTERNAL_ERROR.on(this, "Could not cast element: Expected ${E::class.simpleName} actual $this")
                null
            }
        }
    }

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: Visitor)

    override fun toString(): String {
        return "${this::class.simpleName}"
    }
}
