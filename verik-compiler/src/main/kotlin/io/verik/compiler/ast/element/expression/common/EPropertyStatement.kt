/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a property statement.
 */
class EPropertyStatement(
    override val location: SourceLocation,
    var property: EProperty
) : EExpression(), DeclarationContainer {

    init {
        property.parent = this
    }

    override var type = Core.Kt.C_Unit.toType()

    override val serializationKind = SerializationKind.STATEMENT

    override fun accept(visitor: Visitor) {
        visitor.visitPropertyStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
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
