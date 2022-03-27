/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EEnumEntry(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    var expression: EExpression?
) : EAbstractProperty(), ExpressionContainer {

    override val endLocation = location

    init {
        expression?.parent = this
    }

    fun fill(
        type: Type,
        annotationEntries: List<AnnotationEntry>,
        documentationLines: List<String>?,
        expression: EExpression?
    ) {
        expression?.parent = this
        this.type = type
        this.annotationEntries = annotationEntries
        this.documentationLines = documentationLines
        this.expression = expression
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEnumEntry(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        expression?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
