/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.property.SerializationKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.common.replaceIfContains
import io.verik.compiler.message.SourceLocation

/**
 * Element that represents a call expression to [reference]. Call expression may act on a [receiver].
 */
class ECallExpression(
    override val location: SourceLocation,
    override var type: Type,
    override var reference: Declaration,
    override var receiver: EExpression?,
    override var isSafeAccess: Boolean,
    var valueArguments: ArrayList<EExpression>,
    var typeArguments: ArrayList<Type>
) : EReceiverExpression() {

    override val serializationKind = SerializationKind.EXPRESSION

    init {
        receiver?.parent = this
        valueArguments.forEach { it.parent = this }
    }

    override fun accept(visitor: Visitor) {
        visitor.visitCallExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        super.acceptChildren(visitor)
        valueArguments.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        return if (super.replaceChild(oldExpression, newExpression)) true
        else valueArguments.replaceIfContains(oldExpression, newExpression)
    }
}
