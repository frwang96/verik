/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.common.Visitor

/**
 * Abstract descriptor that contains another [descriptor].
 */
abstract class EContainerDescriptor : EDescriptor(), DescriptorContainer {

    abstract var descriptor: EDescriptor

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
