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
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.property.SuperTypeCallEntry
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.NullDeclaration
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.SourceLocation

class EKtConstructor(
    override val location: SourceLocation
) : EKtAbstractFunction() {

    override var name = "<init>"

    override var type: Type = NullDeclaration.toType()
    override var body: EExpression? = null
    override var valueParameters: ArrayList<EKtValueParameter> = ArrayList()
    override var typeParameters: ArrayList<ETypeParameter> = ArrayList()
    var superTypeCallEntry: SuperTypeCallEntry? = null

    fun init(
        type: Type,
        body: EExpression?,
        valueParameters: List<EKtValueParameter>,
        typeParameters: List<ETypeParameter>,
        superTypeCallEntry: SuperTypeCallEntry?
    ) {
        body?.parent = this
        valueParameters.forEach { it.parent = this }
        superTypeCallEntry?.valueArguments?.forEach { it.parent = this }
        this.type = type
        this.body = body
        this.valueParameters = ArrayList(valueParameters)
        this.typeParameters = ArrayList(typeParameters)
        this.superTypeCallEntry = superTypeCallEntry
    }

    override fun accept(visitor: Visitor) {
        visitor.visitKtConstructor(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        superTypeCallEntry?.valueArguments?.forEach { it.accept(visitor) }
    }
}
