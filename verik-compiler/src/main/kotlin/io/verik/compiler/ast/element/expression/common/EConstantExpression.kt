/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a constant expression.
 */
class EConstantExpression(
    override val location: SourceLocation,
    override var type: Type,
    var value: String
) : EExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    override fun accept(visitor: Visitor) {
        visitor.visitConstantExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
