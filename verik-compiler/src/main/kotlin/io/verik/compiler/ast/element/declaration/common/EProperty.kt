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

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EProperty(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    override var documentationLines: List<String>?,
    var initializer: EExpression?,
    var isMutable: Boolean,
    var isStatic: Boolean
) : EAbstractProperty(), ExpressionContainer {

    init {
        initializer?.parent = this
    }

    fun fill(
        type: Type,
        annotationEntries: List<AnnotationEntry>,
        documentationLines: List<String>?,
        initializer: EExpression?,
        isMutable: Boolean
    ) {
        initializer?.parent = this
        this.type = type
        this.annotationEntries = annotationEntries
        this.documentationLines = documentationLines
        this.initializer = initializer
        this.isMutable = isMutable
        isStatic = false
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitProperty(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        initializer?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (initializer == oldExpression) {
            initializer = newExpression
            true
        } else false
    }

    companion object {

        fun temporary(
            location: SourceLocation,
            type: Type,
            initializer: EExpression?,
            isMutable: Boolean
        ): EProperty {
            return EProperty(
                location = location,
                endLocation = location,
                name = "<tmp>",
                type = type,
                annotationEntries = listOf(),
                documentationLines = null,
                initializer = initializer,
                isMutable = isMutable,
                isStatic = false
            )
        }

        fun named(
            location: SourceLocation,
            name: String,
            type: Type,
            initializer: EExpression?,
            isMutable: Boolean
        ): EProperty {
            return EProperty(
                location = location,
                endLocation = location,
                name = name,
                type = type,
                annotationEntries = listOf(),
                documentationLines = null,
                initializer = initializer,
                isMutable = isMutable,
                isStatic = false
            )
        }
    }
}
