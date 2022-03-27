/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.ast.element.declaration

import io.verik.importer.ast.common.TypeParameterized
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.Visitor
import io.verik.importer.message.SourceLocation

class EKtClass(
    override val location: SourceLocation,
    override val name: String,
    override var signature: String?,
    override var declarations: ArrayList<EDeclaration>,
    override val typeParameters: List<ETypeParameter>,
    val valueParameters: List<EKtValueParameter>,
    var superDescriptor: EDescriptor,
    val isOpen: Boolean
) : EContainerDeclaration(), TypeParameterized {

    init {
        declarations.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        valueParameters.forEach { it.parent = this }
        superDescriptor.parent = this
    }

    fun getConstructor(): EKtConstructor? {
        declarations.forEach {
            if (it is EKtConstructor) {
                return it
            }
        }
        return null
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtClass(this)
    }

    override fun acceptChildren(visitor: Visitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
        valueParameters.forEach { it.accept(visitor) }
        superDescriptor.accept(visitor)
    }
}
