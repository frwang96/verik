/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog module declaration.
 */
class EModule(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override val ports: List<EPort>,
    override var declarations: ArrayList<EDeclaration>
) : EAbstractContainerComponent() {

    init {
        ports.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitModule(this)
    }
}
