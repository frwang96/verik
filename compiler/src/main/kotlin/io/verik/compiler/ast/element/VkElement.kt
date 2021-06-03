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

package io.verik.compiler.ast.element

import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.common.Visitor
import io.verik.compiler.main.MessageLocation
import io.verik.compiler.main.getMessageLocation
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.KtElement

abstract class VkElement {

    abstract val location: MessageLocation

    var parent: VkElement? = null

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: TreeVisitor)
}

inline fun <reified T: VkElement> VkElement.cast(): T? {
    return this.cast(this.location)
}

inline fun <reified T: VkElement> VkElement?.cast(location: KtElement): T? {
    return this.cast(location.getMessageLocation())
}

inline fun <reified T: VkElement> VkElement?.cast(location: VkElement): T? {
    return this.cast(location.location)
}

inline fun <reified T: VkElement> VkElement?.cast(location: MessageLocation): T? {
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
