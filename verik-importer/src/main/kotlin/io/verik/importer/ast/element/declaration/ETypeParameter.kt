/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a type parameter.
 */
class ETypeParameter(
    override val location: SourceLocation,
    override val name: String,
    val descriptor: EDescriptor?,
    val isCardinal: Boolean
) : EDeclaration() {

    override var signature: String? = null

    init {
        descriptor?.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitTypeParameter(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        descriptor?.accept(visitor)
    }
}
