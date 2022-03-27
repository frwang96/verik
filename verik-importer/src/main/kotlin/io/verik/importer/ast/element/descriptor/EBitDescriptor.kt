/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EBitDescriptor(
    override val location: SourceLocation,
    override var type: Type,
    val left: EDescriptor,
    val right: EDescriptor,
    val isSigned: Boolean
) : EDescriptor() {

    init {
        left.parent = this
        right.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitBitDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        left.accept(visitor)
        right.accept(visitor)
    }
}
