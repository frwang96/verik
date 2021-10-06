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
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EStruct(
    override val location: SourceLocation,
    override var name: String,
    val properties: List<ESvProperty>
) : EAbstractClass() {

    init {
        properties.forEach { it.parent = this }
    }

    override var supertype = Core.Kt.C_Any.toType()

    override var typeParameters = arrayListOf<ETypeParameter>()

    override fun accept(visitor: Visitor) {
        visitor.visitStruct(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        properties.forEach { it.accept(visitor) }
    }
}
