/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.ast.kt.element

import io.verik.importer.common.KtVisitor
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

abstract class KtElement {

    abstract val location: SourceLocation

    var parent: KtElement? = null

    fun parentNotNull(): KtElement {
        return parent
            ?: Messages.INTERNAL_ERROR.on(this, "Parent element of $this should not be null")
    }

    inline fun <reified E : KtElement> cast(): E {
        return when (this) {
            is E -> this
            else -> {
                Messages.INTERNAL_ERROR.on(this, "Could not cast element: Expected ${E::class.simpleName} actual $this")
            }
        }
    }

    abstract fun accept(visitor: KtVisitor)

    abstract fun acceptChildren(visitor: KtVisitor)

    override fun toString(): String {
        return "${this::class.simpleName}"
    }
}
