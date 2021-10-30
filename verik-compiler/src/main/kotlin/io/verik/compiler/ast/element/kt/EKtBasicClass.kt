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
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtBasicClass(
    override val location: SourceLocation,
    override var name: String
) : EAbstractContainerClass(), TypeParameterized, Annotated {

    override var superType = NullDeclaration.toType()
    override var declarations: ArrayList<EDeclaration> = arrayListOf()
    override var typeParameters: ArrayList<ETypeParameter> = arrayListOf()
    override var annotations: List<EAnnotation> = listOf()
    var isEnum: Boolean = false
    var isAbstract: Boolean = false
    var isObject: Boolean = false
    var primaryConstructor: EPrimaryConstructor? = null
    var superTypeCallEntry: SuperTypeCallEntry? = null

    fun init(
        superType: Type,
        declarations: List<EDeclaration>,
        typeParameters: List<ETypeParameter>,
        annotations: List<EAnnotation>,
        isEnum: Boolean,
        isAbstract: Boolean,
        isObject: Boolean,
        primaryConstructor: EPrimaryConstructor?,
        superTypeCallEntry: SuperTypeCallEntry?
    ) {
        declarations.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        annotations.forEach { it.parent = this }
        primaryConstructor?.parent = this
        superTypeCallEntry?.valueArguments?.forEach { it.parent = this }
        this.superType = superType
        this.declarations = ArrayList(declarations)
        this.typeParameters = ArrayList(typeParameters)
        this.annotations = annotations
        this.isEnum = isEnum
        this.isAbstract = isAbstract
        this.isObject = isObject
        this.primaryConstructor = primaryConstructor
        this.superTypeCallEntry = superTypeCallEntry
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtBasicClass(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
        annotations.forEach { it.accept(visitor) }
        primaryConstructor?.accept(visitor)
        superTypeCallEntry?.valueArguments?.forEach { it.accept(visitor) }
    }
}
