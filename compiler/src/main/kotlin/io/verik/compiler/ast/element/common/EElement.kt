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

package io.verik.compiler.ast.element.common

import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m

abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    fun parentNotNull(): EElement {
        val parent = parent
        return if (parent != null) parent
        else {
            m.error("Parent element of ${this::class.simpleName} should not be null", this)
            ENullElement(location)
        }
    }

    inline fun <reified E> cast(): E? {
        return when (this) {
            is E -> this
            else -> {
                val expectedName = E::class.simpleName
                val actualName = this::class.simpleName
                m.error("Could not cast element: Expected $expectedName actual $actualName", location)
                null
            }
        }
    }

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: TreeVisitor)
}
