/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin for statement.
 */
class EKtForStatement(
    override val location: SourceLocation,
    val valueParameter: EKtValueParameter,
    var range: EExpression,
    var body: EBlockExpression
) : EExpression(), ExpressionContainer {

    override var type = Core.Kt.C_Unit.toType()

    override val serializationKind = SerializationKind.INTERNAL

    init {
        valueParameter.parent = this
        range.parent = this
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtForStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        valueParameter.accept(visitor)
        range.accept(visitor)
        body.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            range -> {
                range = newExpression
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
