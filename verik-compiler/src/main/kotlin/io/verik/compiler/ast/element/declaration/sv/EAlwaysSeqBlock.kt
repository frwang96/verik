/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.sv.EEventControlExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog always_ff block declaration.
 */
class EAlwaysSeqBlock(
    override val location: SourceLocation,
    override var name: String,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var body: EBlockExpression,
    var eventControlExpression: EEventControlExpression
) : EAbstractProceduralBlock() {

    init {
        body.parent = this
        eventControlExpression.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitAlwaysSeqBlock(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        eventControlExpression.accept(visitor)
    }
}
