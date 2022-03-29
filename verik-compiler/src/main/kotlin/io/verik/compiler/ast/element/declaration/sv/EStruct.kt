/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog packed struct declaration.
 */
class EStruct(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    val properties: List<EProperty>
) : EAbstractClass() {

    init {
        properties.forEach { it.parent = this }
    }

    override var superType = Target.C_Void.toType()

    override fun accept(visitor: Visitor) {
        visitor.visitStruct(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        properties.forEach { it.accept(visitor) }
    }
}
