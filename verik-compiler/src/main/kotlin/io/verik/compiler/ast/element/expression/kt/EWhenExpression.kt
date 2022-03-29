/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.ast.property.WhenEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a Kotlin when expression.
 */
class EWhenExpression(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var type: Type,
    var subject: EExpression?,
    var entries: List<WhenEntry>
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.INTERNAL

    init {
        subject?.parent = this
        entries.forEach { entry ->
            entry.conditions.forEach { it.parent = this }
            entry.body.parent = this
        }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitWhenExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        subject?.accept(visitor)
        entries.forEach { entry ->
            entry.conditions.forEach { it.accept(visitor) }
            entry.body.accept(visitor)
        }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        if (subject == oldExpression) {
            subject = newExpression
            return true
        }
        entries.forEach { entry ->
            if (entry.conditions.replaceIfContains(oldExpression, newExpression))
                return true
            if (entry.body == oldExpression) {
                entry.body = newExpression.cast()
                return true
            }
        }
        return false
    }
}
