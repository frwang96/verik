/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
