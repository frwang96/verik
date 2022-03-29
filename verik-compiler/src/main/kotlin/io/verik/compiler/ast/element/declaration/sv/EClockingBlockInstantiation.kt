/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog clocking block instantiation.
 */
class EClockingBlockInstantiation(
    override val location: SourceLocation,
    override val endLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override val valueArguments: ArrayList<EExpression>,
    var eventControlExpression: EEventControlExpression
) : EAbstractComponentInstantiation() {

    init {
        valueArguments.forEach { it.parent = this }
        eventControlExpression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitClockingBlockInstantiation(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        eventControlExpression.accept(visitor)
    }
}
