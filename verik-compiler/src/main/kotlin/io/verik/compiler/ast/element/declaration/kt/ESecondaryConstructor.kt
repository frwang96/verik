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
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class ESecondaryConstructor(
    override val location: SourceLocation,
    override var name: String,
    override var type: Type,
    override var documentationLines: List<String>?,
    override var body: EBlockExpression,
    override var valueParameters: ArrayList<EKtValueParameter>,
    var superTypeCallExpression: ECallExpression?
) : EKtAbstractFunction() {

    override var annotationEntries: List<AnnotationEntry> = listOf()

    init {
        body.parent = this
        valueParameters.forEach { it.parent = this }
        superTypeCallExpression?.parent = this
    }

    fun fill(
        type: Type,
        documentationLines: List<String>?,
        body: EBlockExpression,
        valueParameters: List<EKtValueParameter>,
        superTypeCallExpression: ECallExpression?
    ) {
        body.parent = this
        valueParameters.forEach { it.parent = this }
        superTypeCallExpression?.parent = this
        this.type = type
        this.documentationLines = documentationLines
        this.body = body
        this.valueParameters = ArrayList(valueParameters)
        this.superTypeCallExpression = superTypeCallExpression
    }

    override fun accept(visitor: Visitor) {
        visitor.visitSecondaryConstructor(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        superTypeCallExpression?.accept(visitor)
    }
}