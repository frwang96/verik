/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a Kotlin package. The package contains [files].
 */
class EKtPackage(
    override val location: SourceLocation,
    override val name: String,
    val files: List<EKtFile>
) : EDeclaration() {

    override var signature: String? = null

    init {
        files.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtPackage(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        files.forEach { it.accept(visitor) }
    }
}
