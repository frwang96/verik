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

import io.verik.compiler.ast.interfaces.Declaration
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.interfaces.Reference
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.main.m

class EValueArgument(
    override val location: SourceLocation,
    override var reference: Declaration,
    var expression: EExpression
) : EElement(), ExpressionContainer, Reference {

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitValueArgument(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression) {
        newExpression.parent = this
        if (expression == oldExpression)
            expression = newExpression
        else
            m.error("Could not find $oldExpression in $this", this)
    }

    fun copy(): EValueArgument {
        val copyExpression = expression.copy()
        return EValueArgument(location, reference, copyExpression)
    }
}