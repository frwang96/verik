/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.descriptor

import io.verik.importer.ast.common.Type
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class ENothingDescriptor(
    override val location: SourceLocation
) : EDescriptor() {

    override var type = Type.unresolved()

    override fun accept(visitor: Visitor) {
        visitor.visitNothingDescriptor(this)
    }

    override fun acceptChildren(visitor: Visitor) {}
}
