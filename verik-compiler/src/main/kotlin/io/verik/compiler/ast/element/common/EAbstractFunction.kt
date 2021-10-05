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
import io.verik.compiler.message.Messages

abstract class EAbstractFunction : EDeclaration(), ExpressionContainer {

    abstract var body: EExpression?

    override fun acceptChildren(visitor: TreeVisitor) {
        body?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (body == oldExpression) {
            body = newExpression
            true
        } else false
    }

    fun getBodyNotNull(): EExpression {
        val body = body
        return if (body != null) {
            body
        } else {
            Messages.INTERNAL_ERROR.on(this, "Function body expected")
            ENullExpression(location)
        }
    }
}
