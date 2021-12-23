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

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtFunction(
    override val location: SourceLocation,
    override var name: String
) : EKtAbstractFunction(), Annotated {

    override var type = NullDeclaration.toType()
    override var body: EAbstractBlockExpression? = null
    override var valueParameters: ArrayList<EKtValueParameter> = ArrayList()
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    var isAbstract: Boolean = false
    var isOverride: Boolean = false

    fun init(
        type: Type,
        body: EAbstractBlockExpression?,
        valueParameters: List<EKtValueParameter>,
        typeParameters: List<ETypeParameter>,
        annotationEntries: List<AnnotationEntry>,
        isAbstract: Boolean,
        isOverride: Boolean
    ) {
        body?.parent = this
        valueParameters.forEach { it.parent = this }
        typeParameters.forEach { it.parent = this }
        this.type = type
        this.body = body
        this.valueParameters = ArrayList(valueParameters)
        this.typeParameters = ArrayList(typeParameters)
        this.annotationEntries = annotationEntries
        this.isAbstract = isAbstract
        this.isOverride = isOverride
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtFunction(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        typeParameters.forEach { it.accept(visitor) }
    }
}
