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

package io.verik.compiler.ast.element.expression.kt

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.core.common.Core
import io.verik.compiler.message.SourceLocation

class EFunctionLiteralExpression(
    override val location: SourceLocation,
    val valueParameters: ArrayList<EAbstractValueParameter>,
    var body: EBlockExpression
) : EExpression(), ExpressionContainer, DeclarationContainer {

    override var type = Core.Kt.C_Function.toType()

    override val serializationKind = SerializationKind.INTERNAL

    init {
        valueParameters.forEach { it.parent = this }
        body.parent = this
    }

    override fun accept(visitor: Visitor) {
        visitor.visitFunctionLiteralExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        valueParameters.forEach { it.accept(visitor) }
        body.accept(visitor)
    }

    override fun childBlockExpressionShouldBeReduced(blockExpression: EBlockExpression): Boolean {
        return false
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (body == oldExpression) {
            body = newExpression.cast()
            true
        } else false
    }

    override fun replaceChild(oldDeclaration: EDeclaration, newDeclaration: EDeclaration): Boolean {
        newDeclaration.parent = this
        return if (oldDeclaration is EAbstractValueParameter && newDeclaration is EAbstractValueParameter) {
            valueParameters.replaceIfContains(oldDeclaration, newDeclaration)
        } else false
    }
}
