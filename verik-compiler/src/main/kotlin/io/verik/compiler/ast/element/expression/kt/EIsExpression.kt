/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
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

    override val serializationKind = SerializationKind.INTERNAL

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
