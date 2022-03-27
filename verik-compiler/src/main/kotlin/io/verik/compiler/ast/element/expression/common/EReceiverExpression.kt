/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.common.TreeVisitor

abstract class EReceiverExpression : EExpression(), ExpressionContainer {

    abstract var reference: Declaration
    abstract var receiver: EExpression?
    abstract var isSafeAccess: Boolean

    override fun acceptChildren(visitor: TreeVisitor) {
        receiver?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (receiver == oldExpression) {
            receiver = newExpression
            true
        } else false
    }
}
