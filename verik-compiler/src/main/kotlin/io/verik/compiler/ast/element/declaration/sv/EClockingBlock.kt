/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog clocking block declaration. It represents the type of an
 * [EClockingBlockInstantiation] and is not serialized as SystemVerilog.
 */
class EClockingBlock(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override val ports: List<EPort>,
    val eventValueParameterIndex: Int
) : EAbstractComponent() {

    init {
        ports.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitClockingBlock(this)
    }
}
