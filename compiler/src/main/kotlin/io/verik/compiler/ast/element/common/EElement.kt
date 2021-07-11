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
import io.verik.compiler.common.getSourceLocation
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.KtElement

abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: TreeVisitor)
}

inline fun <reified T : EElement> EElement.cast(): T? {
    return this.cast(this.location)
}

inline fun <reified T : EElement> EElement?.cast(location: KtElement): T? {
    return this.cast(location.getSourceLocation())
}

inline fun <reified T : EElement> EElement?.cast(location: EElement): T? {
    return this.cast(location.location)
}

inline fun <reified T : EElement> EElement?.cast(location: SourceLocation): T? {
    val expectedName = T::class.simpleName
    return when (this) {
        null -> {
            m.error("Could not cast element: Expected $expectedName actual null", location)
            null
        }
        is T -> this
        else -> {
            val actualName = this::class.simpleName
            m.error("Could not cast element: Expected $expectedName actual $actualName", location)
            null
        }
    }
}
