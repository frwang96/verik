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

package io.verik.compiler.ast.element.sv

import io.verik.compiler.ast.element.common.EAbstractClass
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.target.common.Target

class EStruct(
    override val location: SourceLocation,
    override val bodyStartLocation: SourceLocation,
    override val bodyEndLocation: SourceLocation,
    override var name: String,
    override var type: Type,
    override var annotationEntries: List<AnnotationEntry>,
    val properties: List<EProperty>
) : EAbstractClass() {

    init {
        properties.forEach { it.parent = this }
    }

    override var superType = Target.C_Void.toType()

    override fun accept(visitor: Visitor) {
        visitor.visitStruct(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        properties.forEach { it.accept(visitor) }
    }
}
