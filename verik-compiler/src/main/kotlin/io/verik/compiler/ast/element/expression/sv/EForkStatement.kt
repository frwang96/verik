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

class EForkStatement(
    override val location: SourceLocation,
    var body: EBlockExpression
) : EExpression(), ExpressionContainer {

    override var type = Target.C_Void.toType()

    override val serializationKind = SerializationKind.STATEMENT

    init {
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitForkStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        body.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return false
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (body == oldExpression) {
            body = newExpression.cast()
            true
        } else false
    }
}
