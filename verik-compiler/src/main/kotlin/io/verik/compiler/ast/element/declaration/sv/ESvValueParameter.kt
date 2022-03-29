/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog value parameter declaration.
 */
class ESvValueParameter(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var expression: EExpression?,
    val kind: ValueParameterKind
) : EAbstractValueParameter() {

    override val endLocation = location

    init {
        expression?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvValueParameter(this)
    }
}
