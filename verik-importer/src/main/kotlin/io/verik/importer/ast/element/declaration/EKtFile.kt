/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

class EKtFile(
    override val location: SourceLocation,
    override var declarations: ArrayList<EDeclaration>,
    val outputPath: Path
) : EContainerDeclaration() {

    override var name = outputPath.fileName.toString()
    override var signature: String? = null

    init {
        declarations.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtFile(this)
    }
}
