/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog array literal expression with a [default] value. For an expression that does
 * not use a default value see [EStructLiteralExpression].
 */
class EArrayLiteralExpression(
    override val location: SourceLocation,
    override var type: Type,
    val default: String
) : EExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitArrayLiteralExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
