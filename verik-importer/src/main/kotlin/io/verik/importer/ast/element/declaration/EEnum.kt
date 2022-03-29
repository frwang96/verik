/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents an enum declaration.
 */
class EEnum(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    val entries: List<EEnumEntry>
) : ETypeDeclaration() {

    init {
        entries.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitEnum(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        entries.forEach { it.accept(visitor) }
    }
}
