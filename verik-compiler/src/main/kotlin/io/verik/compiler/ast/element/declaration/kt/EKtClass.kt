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

package io.verik.compiler.ast.element.declaration.kt

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.ETypeParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtClass(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    override var superType: Type,
    override var typeParameters: ArrayList<ETypeParameter>,
    override var declarations: ArrayList<EDeclaration>,
    var primaryConstructor: EPrimaryConstructor?,
    var isEnum: Boolean,
    var isObject: Boolean
) : EAbstractContainerClass() {

    init {
        typeParameters.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
        primaryConstructor?.parent = this
    }

    fun fill(
        type: Type,
        annotationEntries: List<AnnotationEntry>,
        documentationLines: List<String>?,
        superType: Type,
        typeParameters: List<ETypeParameter>,
        declarations: List<EDeclaration>,
        primaryConstructor: EPrimaryConstructor?,
        isEnum: Boolean,
        isObject: Boolean
    ) {
        typeParameters.forEach { it.parent = this }
        declarations.forEach { it.parent = this }
        primaryConstructor?.parent = this
        this.type = type
        this.annotationEntries = annotationEntries
        this.documentationLines = documentationLines
        this.superType = superType
        this.typeParameters = ArrayList(typeParameters)
        this.declarations = ArrayList(declarations)
        this.primaryConstructor = primaryConstructor
        this.isEnum = isEnum
        this.isObject = isObject
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtClass(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        primaryConstructor?.accept(visitor)
    }
}
