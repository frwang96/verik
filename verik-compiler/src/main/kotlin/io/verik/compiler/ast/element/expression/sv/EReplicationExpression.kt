/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractContainerExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EReplicationExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var expression: EExpression,
    var value: Int
) : EAbstractContainerExpression() {

    init {
        expression.parent = this
    }

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitReplicationExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
