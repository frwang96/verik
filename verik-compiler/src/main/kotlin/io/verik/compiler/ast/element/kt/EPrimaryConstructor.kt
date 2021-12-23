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

import io.verik.compiler.ast.element.common.EAbstractBlockExpression
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EPrimaryConstructor(
    override val location: SourceLocation
) : EKtAbstractFunction() {

    override var name = "<init>"
    override var body: EAbstractBlockExpression? = null

    override var type: Type = NullDeclaration.toType()
    override var valueParameters: ArrayList<EKtValueParameter> = ArrayList()
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()

    fun init(type: Type, valueParameters: List<EKtValueParameter>, typeParameters: List<ETypeParameter>) {
        valueParameters.forEach { it.parent = this }
        this.type = type
        this.valueParameters = ArrayList(valueParameters)
        this.typeParameters = ArrayList(typeParameters)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPrimaryConstructor(this)
    }
}
