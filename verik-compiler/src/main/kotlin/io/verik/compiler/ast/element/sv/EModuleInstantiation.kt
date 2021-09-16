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

package io.verik.compiler.ast.element.sv

import io.verik.compiler.ast.element.common.EAbstractProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.PortInstantiation
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

class EModuleInstantiation(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    val portInstantiations: List<PortInstantiation>
) : EAbstractProperty(), ExpressionContainer {

    init {
        portInstantiations.forEach { it.expression?.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitModuleInstantiation(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        portInstantiations.forEach { it.expression?.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression) {
        newExpression.parent = this
        portInstantiations.forEach {
            if (it.expression == oldExpression) {
                it.expression = newExpression
                return
            }
        }
        Messages.INTERNAL_ERROR.on(this, "Could not find $oldExpression in $this")
    }
}
