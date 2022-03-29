/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

/**
 * Element that represents a SystemVerilog cover group declaration.
 */
class ECoverGroup(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var typeParameters: ArrayList<ETypeParameter>,
    override var declarations: ArrayList<EDeclaration>,
    var constructor: ESvConstructor
) : EAbstractContainerClass() {

    override var superType = Target.C_Void.toType()

    init {
        typeParameters.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
        constructor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCoverGroup(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        constructor.accept(visitor)
    }
}
