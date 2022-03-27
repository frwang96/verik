/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class ESvForStatement(
    override val location: SourceLocation,
    var property: EProperty,
    var condition: EExpression,
    var iteration: EExpression,
    var body: EBlockExpression
) : EExpression(), DeclarationContainer, ExpressionContainer {

    override var type = Target.C_Void.toType()

    override val serializationKind = SerializationKind.STATEMENT

    init {
        property.parent = this
        condition.parent = this
        iteration.parent = this
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvForStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        property.accept(visitor)
        condition.accept(visitor)
        iteration.accept(visitor)
        body.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return (blockExpression == condition) || (blockExpression == iteration)
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return if (property == oldDeclaration) {
            property = newDeclaration.cast()
            true
        } else false
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            condition -> {
                condition = newExpression
                true
            }
            iteration -> {
                iteration = newExpression
                true
            }
            body -> {
                body = newExpression.cast()
                true
            }
            else -> false
        }
    }
}
