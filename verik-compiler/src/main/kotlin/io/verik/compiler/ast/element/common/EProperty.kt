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

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.element.sv.ESvClass
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.interfaces.ExpressionContainer
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EProperty(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    var initializer: EExpression?,
    var isMutable: Boolean
) : EAbstractProperty(), Annotated, ExpressionContainer {

    init {
        initializer?.parent = this
    }

    fun fill(
        type: Type,
        initializer: EExpression?,
        annotationEntries: List<AnnotationEntry>,
        isMutable: Boolean
    ) {
        initializer?.parent = this
        this.type = type
        this.initializer = initializer
        this.annotationEntries = annotationEntries
        this.isMutable = isMutable
    }

    fun isStatic(): Boolean {
        val parent = parent
        return (parent is ESvClass && parent.isDeclarationsStatic)
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

        fun getTemporary(
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
                initializer = initializer,
                isMutable = isMutable
            )
        }
    }
}
