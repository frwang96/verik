/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog immediate assert statement.
 */
class EImmediateAssertStatement(
    override val location: SourceLocation,
    var condition: EExpression,
    var elseExpression: EBlockExpression?
) : EExpression(), ExpressionContainer {

    override var type = Target.C_Void.toType()
    override val serializationKind = SerializationKind.STATEMENT

    init {
        condition.parent = this
        elseExpression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitImmediateAssertStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        elseExpression?.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return (blockExpression == condition)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            condition -> {
                condition = newExpression
                true
            }
            elseExpression -> {
                elseExpression = newExpression.cast()
                true
            }
            else -> false
        }
    }
}
