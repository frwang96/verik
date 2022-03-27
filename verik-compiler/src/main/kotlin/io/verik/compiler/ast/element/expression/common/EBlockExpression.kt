/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EBlockExpression(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var type: Type,
    var statements: ArrayList<EExpression>
) : EExpression(), ExpressionContainer {

    override val serializationKind = SerializationKind.STATEMENT

    init {
        statements.forEach { it.parent = this }
    }

    fun getOnlyStatement(): EExpression? {
        return if (statements.size == 1) {
            val statement = statements[0]
            if (statement is EBlockExpression) {
                statement.getOnlyStatement()
            } else statement
        } else null
    }

    fun isEmpty(): Boolean {
        return statements.all { it is EBlockExpression && it.isEmpty() }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitBlockExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        statements.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return statements.replaceIfContains(oldExpression, newExpression)
    }

    companion object {

        fun empty(location: SourceLocation): EBlockExpression {
            return EBlockExpression(location, location, Core.Kt.C_Unit.toType(), ArrayList())
        }

        fun wrap(expression: EExpression): EBlockExpression {
            return if (expression !is EBlockExpression) {
                EBlockExpression(
                    expression.location,
                    expression.location,
                    expression.type.copy(),
                    arrayListOf(expression)
                )
            } else expression
        }

        fun extract(expression: EExpression, extractedExpressions: List<EExpression>) {
            val blockExpression = EBlockExpression(
                expression.location,
                expression.location,
                expression.type.copy(),
                ArrayList(extractedExpressions)
            )
            expression.replace(blockExpression)
        }
    }
}
