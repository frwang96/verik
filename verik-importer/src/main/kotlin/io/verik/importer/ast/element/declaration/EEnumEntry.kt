/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EEnumEntry(
    override val location: SourceLocation,
    override val name: String
) : EDeclaration() {

    override var signature: String? = null

    override fun accept(visitor: Visitor) {
        visitor.visitEnumEntry(this)
    }

    override fun acceptChildren(visitor: Visitor) {}
}
