/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.property.AnnotationEntry
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EKtFunction(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override val valueParameters: List<EKtValueParameter>,
    val descriptor: EDescriptor,
    val annotationEntries: List<AnnotationEntry>,
    val isOpen: Boolean,
    var isOverride: Boolean
) : EKtAbstractFunction() {

    init {
        valueParameters.forEach { it.parent = this }
        descriptor.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtFunction(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        descriptor.accept(visitor)
    }
}
