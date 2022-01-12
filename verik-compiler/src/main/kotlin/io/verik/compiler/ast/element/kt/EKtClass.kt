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
import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtClass(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var superType: Type,
    override var declarations: ArrayList<EDeclaration>,
    override var typeParameters: ArrayList<ETypeParameter>,
    override var annotationEntries: List<AnnotationEntry>,
    var isEnum: Boolean,
    var isAbstract: Boolean,
    var isObject: Boolean,
    var primaryConstructor: EPrimaryConstructor?,
    var superTypeCallExpression: ECallExpression?,
) : EAbstractContainerClass(), TypeParameterized, Annotated {

    init {
        declarations.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        primaryConstructor?.parent = this
        superTypeCallExpression?.parent = this
    }

    fun fill(
        type: Type,
        superType: Type,
        declarations: List<EDeclaration>,
        typeParameters: List<ETypeParameter>,
        annotationEntries: List<AnnotationEntry>,
        isEnum: Boolean,
        isAbstract: Boolean,
        isObject: Boolean,
        primaryConstructor: EPrimaryConstructor?,
        superTypeCallExpression: ECallExpression?
    ) {
        declarations.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        primaryConstructor?.parent = this
        superTypeCallExpression?.parent = this
        this.type = type
        this.superType = superType
        this.declarations = ArrayList(declarations)
        this.typeParameters = ArrayList(typeParameters)
        this.annotationEntries = annotationEntries
        this.isEnum = isEnum
        this.isAbstract = isAbstract
        this.isObject = isObject
        this.primaryConstructor = primaryConstructor
        this.superTypeCallExpression = superTypeCallExpression
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtClass(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
        primaryConstructor?.accept(visitor)
        superTypeCallExpression?.accept(visitor)
    }
}
