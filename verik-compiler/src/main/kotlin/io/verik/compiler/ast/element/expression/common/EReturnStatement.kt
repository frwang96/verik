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

class EReturnStatement(
    override val location: SourceLocation,
    override var type: Type,
    var expression: EExpression?
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.STATEMENT

    init {
        expression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitReturnStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
