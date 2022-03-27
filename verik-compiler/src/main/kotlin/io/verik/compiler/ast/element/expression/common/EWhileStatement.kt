/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EWhileStatement(
    override val location: SourceLocation,
    var condition: EExpression,
    var body: EBlockExpression,
    val isDoWhile: Boolean
) : EExpression(), ExpressionContainer {

    override var type = Core.Kt.C_Unit.toType()

    override val serializationKind = SerializationKind.STATEMENT

    init {
        condition.parent = this
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitWhileStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        body.accept(visitor)
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
            body -> {
                body = newExpression.cast()
                true
            }
            else -> false
        }
    }
}
