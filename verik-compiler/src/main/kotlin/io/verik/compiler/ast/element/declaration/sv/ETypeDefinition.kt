/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EClassifier
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a SystemVerilog typedef declaration.
 */
class ETypeDefinition(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type
) : EClassifier() {

    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null

    override fun accept(visitor: Visitor) {
        visitor.visitTypeDefinition(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {}
}
