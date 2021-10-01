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

import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    fun parentNotNull(): EElement {
        val parent = parent
        return if (parent != null) parent
        else {
            Messages.INTERNAL_ERROR.on(this, "Parent element of $this should not be null")
            ENullElement(location)
        }
    }

    inline fun <reified E> cast(): E? {
        return when (this) {
            is E -> this
            else -> {
                Messages.INTERNAL_ERROR.on(this, "Could not cast element: Expected ${E::class.simpleName} actual $this")
                null
            }
        }
    }

    fun replaceChildAsExpressionContainer(oldExpression: EExpression, newExpression: EExpression) {
        if (this is ExpressionContainer)
            this.replaceChild(oldExpression, newExpression)
        else
            Messages.INTERNAL_ERROR.on(oldExpression, "Could not replace $oldExpression in $this")
    }

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: TreeVisitor)

    override fun toString(): String {
        return "${this::class.simpleName}"
    }
}
