/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.TypeParameterized
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EModule(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override var declarations: ArrayList<EDeclaration>,
    override val typeParameters: List<ETypeParameter>,
    val ports: ArrayList<EPort>,
) : EContainerDeclaration(), TypeParameterized {

    init {
        declarations.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        ports.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitModule(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
        ports.forEach { it.accept(visitor) }
    }
}
