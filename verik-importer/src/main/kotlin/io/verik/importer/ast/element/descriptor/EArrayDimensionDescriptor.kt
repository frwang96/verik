/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents an array dimension descriptor. If [isQueue] is true it is a queue dimension descriptor.
 * Otherwise, it is an unsized dimension descriptor.
 */
class EArrayDimensionDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    override var descriptor: EDescriptor,
    val isQueue: Boolean
) : EContainerDescriptor() {

    init {
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitArrayDimensionDescriptor(this)
    }
}
