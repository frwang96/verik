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
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class ECoverBin(
    override val location: SourceLocation,
    override var name: String,
    var expression: EExpression,
    val isIgnored: Boolean,
    val isArray: Boolean
) : EAbstractProperty(), ExpressionContainer {

    override val endLocation = location
    override var type = Target.C_Void.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        expression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCoverBin(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
