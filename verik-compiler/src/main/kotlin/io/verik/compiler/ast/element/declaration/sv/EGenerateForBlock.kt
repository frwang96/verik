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

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EGenerateForBlock(
    override val location: SourceLocation,
    override val endLocation: SourceLocation,
    override var name: String,
    override var documentationLines: List<String>?,
    val indexProperty: EProperty,
    var declaration: EDeclaration,
    val size: Int
) : EAbstractProperty(), DeclarationContainer {

    override var type = Target.C_Void.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()

    init {
        indexProperty.parent = this
        declaration.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitGenerateForBlock(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        indexProperty.accept(visitor)
        declaration.accept(visitor)
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return if (declaration == oldDeclaration) {
            declaration = newDeclaration
            true
        } else false
    }
}
