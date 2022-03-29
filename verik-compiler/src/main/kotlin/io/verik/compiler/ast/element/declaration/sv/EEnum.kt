/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog enum declaration. While it contains references to [enumEntries], the enum
 * entries are siblings and not children in the AST to account for SystemVerilog scoping rules.
 */
class EEnum(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    val property: EProperty?,
    val enumEntries: List<EEnumEntry>
) : EAbstractClass() {

    override var superType = Target.C_Void.toType()

    init {
        property?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEnum(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        property?.accept(visitor)
    }
}
