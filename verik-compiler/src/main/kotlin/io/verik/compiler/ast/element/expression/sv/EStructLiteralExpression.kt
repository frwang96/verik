/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.StructLiteralEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EStructLiteralExpression(
    override val location: SourceLocation,
    override var type: Type,
    val entries: List<StructLiteralEntry>
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        entries.forEach { it.expression.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitStructLiteralExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        entries.forEach { it.expression.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        entries.forEach {
            if (it.expression == oldExpression) {
                it.expression = newExpression
                return true
            }
        }
        return false
    }
}
