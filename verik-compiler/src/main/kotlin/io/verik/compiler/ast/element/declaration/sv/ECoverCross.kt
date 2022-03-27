/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class ECoverCross(
    override val location: SourceLocation,
    override val endLocation: SourceLocation,
    override var name: String,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    val coverPoints: List<ECoverPoint>,
    val coverBins: List<ECoverBin>
) : EAbstractProperty() {

    override var type = Target.C_Void.toType()

    init {
        coverBins.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCoverCross(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        coverBins.forEach { it.accept(visitor) }
    }
}
