/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents an index dimension descriptor.
 */
class EIndexDimensionDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    override var descriptor: EDescriptor,
    var index: EDescriptor
) : EContainerDescriptor() {

    init {
        descriptor.parent = this
        index.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitIndexDimensionDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        index.accept(visitor)
    }

    override fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean {
        if (super.replaceChild(oldDescriptor, newDescriptor))
            return true
        return if (oldDescriptor == index) {
            index = newDescriptor
            true
        } else false
    }
}
