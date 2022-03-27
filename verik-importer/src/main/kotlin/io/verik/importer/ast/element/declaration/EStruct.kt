/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EStruct(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    val entries: List<EStructEntry>
) : ETypeDeclaration() {

    init {
        entries.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitStruct(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        entries.forEach { it.accept(visitor) }
    }
}
