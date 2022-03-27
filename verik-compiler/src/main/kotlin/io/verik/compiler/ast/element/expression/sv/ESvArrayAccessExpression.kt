/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class ESvArrayAccessExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var array: EExpression,
    var index: EExpression
) : EAbstractArrayAccessExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        array.parent = this
        index.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvArrayAccessExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        index.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            array -> {
                array = newExpression
                true
            }
            index -> {
                index = newExpression
                true
            }
            else -> false
        }
    }
}
