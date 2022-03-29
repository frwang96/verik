/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a SystemVerilog struct entry declaration.
 */
class EStructEntry(
    override val location: SourceLocation,
    override val name: String,
    var descriptor: EDescriptor
) : EDeclaration(), DescriptorContainer {

    override var signature: String? = null

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitStructEntry(this)
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
