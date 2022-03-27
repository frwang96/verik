/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement

abstract class EDescriptor : EElement() {

    abstract var type: Type

    fun replace(descriptor: EDescriptor) {
        parentNotNull().replaceChildAsDescriptorContainer(this, descriptor)
    }

    fun wrap(descriptor: EDescriptor): EDescriptor {
        if (descriptor is ENothingDescriptor)
            return descriptor
        val containerDescriptor = descriptor.cast<EContainerDescriptor>()
        containerDescriptor.descriptor = this
        this.parent = containerDescriptor
        return containerDescriptor
    }
}
