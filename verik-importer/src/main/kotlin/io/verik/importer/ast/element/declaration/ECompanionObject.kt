/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class ECompanionObject(
    override val location: SourceLocation,
    override var declarations: ArrayList<EDeclaration>
) : EContainerDeclaration() {

    override val name = "Companion"
    override var signature: String? = null

    init {
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCompanionObject(this)
    }
}
