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

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationType
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EIsExpression(
    override val location: SourceLocation,
    override var expression: EExpression,
    var property: EProperty,
    val isNegated: Boolean,
    var castType: Type
) : EAbstractContainerExpression(), DeclarationContainer {

    override var type = Core.Kt.C_Boolean.toType()

    override val serializationType = SerializationType.INTERNAL

    init {
        expression.parent = this
        property.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitIsExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        property.accept(visitor)
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return if (property == oldDeclaration) {
            property = newDeclaration.cast()
            true
        } else false
    }
}
