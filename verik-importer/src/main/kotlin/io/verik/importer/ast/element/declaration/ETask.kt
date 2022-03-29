/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a SystemVerilog task declaration.
 */
class ETask(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override val valueParameters: List<ESvValueParameter>,
    override var isStatic: Boolean
) : ESvAbstractFunction() {

    init {
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitTask(this)
    }
}
