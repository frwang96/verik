/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.CaseEntry
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog case statement.
 */
class ECaseStatement(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var type: Type,
    var subject: EExpression,
    val entries: List<CaseEntry>
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.STATEMENT

    init {
        subject.parent = this
        entries.forEach { entry ->
            entry.conditions.forEach { it.parent = this }
            entry.body.parent = this
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCaseStatement(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        subject.accept(visitor)
        entries.forEach { entry ->
            entry.conditions.forEach { it.accept(visitor) }
            entry.body.accept(visitor)
        }
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return (blockExpression == subject)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        if (subject == oldExpression) {
            subject = newExpression
            return true
        }
        entries.forEach {
            if (it.conditions.replaceIfContains(oldExpression, newExpression))
                return true
            if (it.body == oldExpression) {
                it.body = newExpression.cast()
                return true
            }
        }
        return false
    }
}
