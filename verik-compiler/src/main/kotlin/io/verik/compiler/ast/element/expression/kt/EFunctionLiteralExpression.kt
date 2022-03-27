/*
 * SPDX-License-Identifier: Apache-2.0
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
