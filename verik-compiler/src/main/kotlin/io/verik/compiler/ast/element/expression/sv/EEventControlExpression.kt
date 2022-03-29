/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog event control expression.
 */
class EEventControlExpression(
    override val location: SourceLocation,
    var expressions: ArrayList<EExpression>
) : EExpression(), ExpressionContainer {

    override var type = Target.C_Void.toType()

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        expressions.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEventControlExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expressions.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return expressions.replaceIfContains(oldExpression, newExpression)
    }
}
