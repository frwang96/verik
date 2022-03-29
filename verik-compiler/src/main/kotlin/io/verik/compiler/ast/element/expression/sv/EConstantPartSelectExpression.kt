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

/**
 * Element that represents a SystemVerilog constant part select expression. [startIndex] and [endIndex] must be
 * constant expressions.
 */
class EConstantPartSelectExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var array: EExpression,
    var startIndex: EExpression,
    var endIndex: EExpression
) : EAbstractArrayAccessExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        array.parent = this
        startIndex.parent = this
        endIndex.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitConstantPartSelectExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        startIndex.accept(visitor)
        endIndex.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        @Suppress("DuplicatedCode")
        newExpression.parent = this
        return when (oldExpression) {
            array -> {
                array = newExpression
                true
            }
            startIndex -> {
                startIndex = newExpression
                true
            }
            endIndex -> {
                endIndex = newExpression
                true
            }
            else -> false
        }
    }
}
