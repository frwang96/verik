/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.expression.sv.EInjectedExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EInjectedProperty(
    override val location: SourceLocation,
    override var name: String,
    val injectedExpression: EInjectedExpression
) : EAbstractProperty() {

    override val endLocation = location
    override var type = Target.C_Void.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    init {
        injectedExpression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitInjectedProperty(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        injectedExpression.accept(visitor)
    }
}
