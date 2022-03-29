/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a Kotlin type alias declaration or a SystemVerilog typedef declaration.
 */
class ETypeAlias(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    var descriptor: EDescriptor
) : ETypeDeclaration(), DescriptorContainer {

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitTypeAlias(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        descriptor.accept(visitor)
    }

    override fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean {
        newDescriptor.parent = this
        return if (descriptor == oldDescriptor) {
            descriptor = newDescriptor
            true
        } else false
    }
}
