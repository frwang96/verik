/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

/**
 * Element that represents a Kotlin secondary constructor declaration.
 */
class EKtConstructor(
    override val location: SourceLocation,
    override var signature: String?,
    override val valueParameters: List<EKtValueParameter>
) : EKtAbstractFunction() {

    override val name = "new"

    init {
        valueParameters.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtConstructor(this)
    }
}
