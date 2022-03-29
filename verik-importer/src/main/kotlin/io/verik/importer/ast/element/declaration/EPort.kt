/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.property.PortType
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a SystemVerilog port declaration.
 */
class EPort(
    override val location: SourceLocation,
    override val name: String,
    var descriptor: EDescriptor,
    val portType: PortType
) : EDeclaration(), DescriptorContainer {

    override var signature: String? = null

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPort(this)
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
