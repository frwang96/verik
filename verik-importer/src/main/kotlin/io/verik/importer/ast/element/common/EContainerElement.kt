/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.common

import io.verik.importer.common.Visitor
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation

class EContainerElement(
    override val location: SourceLocation,
    val elements: List<EElement>
) : EElement() {

    override fun accept(visitor: Visitor) {
        Messages.INTERNAL_ERROR.on(this, "Container element should not be part of the AST")
    }

    override fun acceptChildren(visitor: Visitor) {
        Messages.INTERNAL_ERROR.on(this, "Container element should not be part of the AST")
    }
}
