/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.ast.element.kt

import io.verik.compiler.ast.element.common.EAbstractContainerClass
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtBasicClass(
    override val location: SourceLocation,
    override var name: String
) : EAbstractContainerClass(), Annotated {

    override var supertype = NullDeclaration.toType()
    override var declarations: ArrayList<EDeclaration> = arrayListOf()
    override var annotations: List<EAnnotation> = listOf()
    var isEnum: Boolean = false
    var typeParameters: ArrayList<ETypeParameter> = arrayListOf()
    var primaryConstructor: EPrimaryConstructor? = null

    fun init(
        supertype: Type,
        declarations: List<EDeclaration>,
        annotations: List<EAnnotation>,
        isEnum: Boolean,
        typeParameters: List<ETypeParameter>,
        primaryConstructor: EPrimaryConstructor?
    ) {
        this.supertype = supertype
        declarations.forEach { it.parent = this }
        this.declarations = ArrayList(declarations)
        annotations.forEach { it.parent = this }
        this.annotations = annotations
        this.isEnum = isEnum
        typeParameters.forEach { it.parent = this }
        this.typeParameters = ArrayList(typeParameters)
        primaryConstructor?.parent = this
        this.primaryConstructor = primaryConstructor
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtBasicClass(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        annotations.forEach { it.accept(visitor) }
        typeParameters.forEach { it.accept(visitor) }
        primaryConstructor?.accept(visitor)
    }
}
