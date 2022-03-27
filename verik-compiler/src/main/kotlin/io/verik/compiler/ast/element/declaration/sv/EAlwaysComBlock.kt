/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EAlwaysComBlock(
    override val location: SourceLocation,
    override var name: String,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var body: EBlockExpression
) : EAbstractProceduralBlock() {

    init {
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitAlwaysComBlock(this)
    }
}
