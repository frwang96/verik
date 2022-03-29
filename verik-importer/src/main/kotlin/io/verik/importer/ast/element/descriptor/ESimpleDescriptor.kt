/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a descriptor of [type].
 */
class ESimpleDescriptor(
    override val location: SourceLocation,
    override var type: Type
) : EDescriptor() {

    override fun accept(visitor: Visitor) {
        visitor.visitSimpleDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {}
}
