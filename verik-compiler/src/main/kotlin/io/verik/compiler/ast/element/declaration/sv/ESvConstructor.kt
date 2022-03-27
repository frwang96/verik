/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class ESvConstructor(
    override val location: SourceLocation,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var body: EBlockExpression,
    override var valueParameters: ArrayList<ESvValueParameter>
) : ESvAbstractFunction() {

    override var name = "new"
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()
    override val isStatic = true

    init {
        body.parent = this
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvConstructor(this)
    }
}
