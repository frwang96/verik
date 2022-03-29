/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class ECoverPoint(
    override val location: SourceLocation,
    override val endLocation: SourceLocation,
    override var name: String,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    var expression: EExpression,
    val binExpressions: ArrayList<EExpression>
) : EAbstractProperty(), ExpressionContainer {

    override var type = Target.C_Void.toType()

    init {
        expression.parent = this
        binExpressions.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCoverPoint(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression.accept(visitor)
        binExpressions.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else binExpressions.replaceIfContains(oldExpression, newExpression)
    }
}
