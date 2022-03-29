/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a SystemVerilog constructor declaration.
 */
class ESvConstructor(
    override val location: SourceLocation,
    override var signature: String?,
    override val valueParameters: List<ESvValueParameter>
) : ESvAbstractFunction() {

    override val name = "new"
    override var isStatic = true

    init {
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSvConstructor(this)
    }
}
