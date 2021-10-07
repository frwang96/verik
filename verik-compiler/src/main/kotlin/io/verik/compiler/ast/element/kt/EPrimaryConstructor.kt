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

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EPrimaryConstructor(
    override val location: SourceLocation
) : EKtAbstractFunction() {

    override var name = "<init>"
    override var body: EExpression? = null

    override var type: Type = NullDeclaration.toType()
    override var valueParameters: ArrayList<EKtValueParameter> = arrayListOf()

    fun init(type: Type, valueParameters: List<EKtValueParameter>) {
        this.type = type
        valueParameters.forEach { it.parent = this }
        this.valueParameters = ArrayList(valueParameters)
    }

    override fun accept(visitor: Visitor) {
        visitor.visitPrimaryConstructor(this)
    }
}
