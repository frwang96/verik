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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EPrimaryConstructor(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var valueParameters: ArrayList<EKtValueParameter>,
    override var typeParameters: ArrayList<ETypeParameter>
) : EKtAbstractFunction() {

    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null
    override var body: EBlockExpression = EBlockExpression.empty(location)

    init {
        valueParameters.forEach { it.parent = this }
        body.parent = this
    }

    fun fill(
        type: Type,
        valueParameters: List<EKtValueParameter>,
        typeParameters: List<ETypeParameter>
    ) {
        valueParameters.forEach { it.parent = this }
        this.type = type
        this.valueParameters = ArrayList(valueParameters)
        this.typeParameters = ArrayList(typeParameters)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPrimaryConstructor(this)
    }
}
