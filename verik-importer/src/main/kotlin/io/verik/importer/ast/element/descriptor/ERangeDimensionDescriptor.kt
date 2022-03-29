/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a range dimension descriptor with MSB index [left] and LSB index [right].
 */
class ERangeDimensionDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    override var descriptor: EDescriptor,
    var left: EDescriptor,
    var right: EDescriptor,
    val isPacked: Boolean
) : EContainerDescriptor() {

    init {
        descriptor.parent = this
        left.parent = this
        right.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitRangeDimensionDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        left.accept(visitor)
        right.accept(visitor)
    }

    override fun replaceChild(oldDescriptor: EDescriptor, newDescriptor: EDescriptor): Boolean {
        if (super.replaceChild(oldDescriptor, newDescriptor))
            return true
        return when (oldDescriptor) {
            left -> {
                left = newDescriptor
                true
            }
            right -> {
                right = newDescriptor
                true
            }
            else -> false
        }
    }
}
