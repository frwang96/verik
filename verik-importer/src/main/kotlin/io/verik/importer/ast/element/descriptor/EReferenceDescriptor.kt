/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a reference descriptor to [reference].
 */
class EReferenceDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    val name: String,
    var reference: Declaration,
    val typeArguments: List<ETypeArgument>
) : EDescriptor() {

    init {
        typeArguments.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitReferenceDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        typeArguments.forEach { it.accept(visitor) }
    }
}
