/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EIfExpression(
    override val location: SourceLocation,
    override var type: Type,
    var condition: EExpression,
    var thenExpression: EBlockExpression?,
    var elseExpression: EBlockExpression?
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.STATEMENT

    init {
        condition.parent = this
        thenExpression?.parent = this
        elseExpression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitIfExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        condition.accept(visitor)
        thenExpression?.accept(visitor)
        elseExpression?.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return (blockExpression == condition)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        @Suppress("DuplicatedCode")
        newExpression.parent = this
        return when (oldExpression) {
            condition -> {
                condition = newExpression
                true
            }
            thenExpression -> {
                thenExpression = newExpression.cast()
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
