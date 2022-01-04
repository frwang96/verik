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

import io.verik.compiler.ast.element.common.EAbstractInitializedProperty
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.interfaces.Annotated
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.NullDeclaration
import io.verik.compiler.message.SourceLocation

class EKtProperty(
    override val location: SourceLocation,
    val endLocation: SourceLocation,
    override var name: String
) : EAbstractInitializedProperty(), Annotated {

    override var type = NullDeclaration.toType()
    override var initializer: EExpression? = null
    override var annotationEntries: List<AnnotationEntry> = listOf()
    var isMutable = false

    fun init(type: Type, initializer: EExpression?, annotationEntries: List<AnnotationEntry>, isMutable: Boolean) {
        initializer?.parent = this
        this.type = type
        this.initializer = initializer
        this.annotationEntries = annotationEntries
        this.isMutable = isMutable
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitKtProperty(this)
    }
}
