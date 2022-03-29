/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a type argument.
 */
class ETypeArgument(
    override val location: SourceLocation,
    val name: String?,
    var descriptor: EDescriptor
) : EElement(), DescriptorContainer {

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitTypeArgument(this)
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
