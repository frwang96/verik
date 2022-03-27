/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.DescriptorContainer
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class ESvFunction(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override val valueParameters: List<ESvValueParameter>,
    override var isStatic: Boolean,
    var descriptor: EDescriptor
) : ESvAbstractFunction(), DescriptorContainer {

    init {
        valueParameters.forEach { it.parent = this }
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvFunction(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
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
