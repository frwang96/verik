/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog constraint declaration. [body] contains the list of constraints.
 */
class EConstraint(
    override val location: SourceLocation,
    override var name: String,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    var body: EBlockExpression
) : EAbstractProperty(), ExpressionContainer {

    override val endLocation = location
    override var type = Target.C_Void.toType()

    init {
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitConstraint(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        body.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (body == oldExpression) {
            body = newExpression.cast()
            true
        } else false
    }
}
