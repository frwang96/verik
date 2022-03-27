/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class ESvPackage(
    override val location: SourceLocation,
    override val name: String,
    override var declarations: ArrayList<EDeclaration>
) : EContainerDeclaration() {

    override var signature: String? = null

    init {
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvPackage(this)
    }
}
