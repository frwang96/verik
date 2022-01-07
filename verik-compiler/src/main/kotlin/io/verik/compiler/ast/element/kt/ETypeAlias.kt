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

import io.verik.compiler.ast.element.common.EClassifier
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.interfaces.TypeParameterized
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.core.common.NullDeclaration
import io.verik.compiler.message.SourceLocation

class ETypeAlias(
    override val location: SourceLocation,
    override var name: String
) : EClassifier(), TypeParameterized {

    override var type = NullDeclaration.toType()
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()

    fun init(type: Type, typeParameters: List<ETypeParameter>) {
        typeParameters.forEach { it.parent = this }
        this.type = type
        this.typeParameters = ArrayList(typeParameters)
    }

    override fun accept(visitor: Visitor) {
        return visitor.visitTypeAlias(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        typeParameters.forEach { it.accept(visitor) }
    }
}
