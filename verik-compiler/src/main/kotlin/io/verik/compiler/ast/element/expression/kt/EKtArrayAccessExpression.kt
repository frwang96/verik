/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EAbstractArrayAccessExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin array expression.
 */
class EKtArrayAccessExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var array: EExpression,
    var indices: ArrayList<EExpression>
) : EAbstractArrayAccessExpression() {

    override val serializationKind = SerializationKind.INTERNAL

    init {
        array.parent = this
        indices.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtArrayAccessExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        indices.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (array == oldExpression) {
            array = newExpression
            true
        } else {
            indices.replaceIfContains(oldExpression, newExpression)
        }
    }
}
