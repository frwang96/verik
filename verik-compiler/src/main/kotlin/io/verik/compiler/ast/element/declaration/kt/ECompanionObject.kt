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

import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class ECompanionObject(
    override val location: SourceLocation,
    override var declarations: ArrayList<EDeclaration>
) : EAbstractContainerClass() {

    override val bodyStartLocation = location
    override val bodyEndLocation = location
    override var name = "Companion"
    override var type = this.toType()
    override var annotationEntries: List<AnnotationEntry> = listOf()
    override var documentationLines: List<String>? = null
    override var superType = Core.Kt.C_Any.toType()

    init {
        declarations.forEach { it.parent = this }
    }

    fun fill(declarations: List<EDeclaration>) {
        declarations.forEach { it.parent = this }
        this.declarations = ArrayList(declarations)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCompanionObject(this)
    }
}
