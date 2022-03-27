/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

class EConcatenationExpression(
    override val location: SourceLocation,
    override var type: Type,
    val expressions: ArrayList<EExpression>
) : EExpression(), ExpressionContainer {

    init {
        expressions.forEach { it.parent = this }
    }

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitConcatenationExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expressions.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return expressions.replaceIfContains(oldExpression, newExpression)
    }
}
