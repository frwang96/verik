/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EModulePortInstantiation(
    override val location: SourceLocation,
    override val endLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override val valueArguments: ArrayList<EExpression>
) : EAbstractComponentInstantiation() {

    init {
        valueArguments.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitModulePortInstantiation(this)
    }
}
