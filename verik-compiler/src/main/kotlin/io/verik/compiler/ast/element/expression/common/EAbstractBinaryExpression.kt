/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.common.TreeVisitor

/**
 * Base class for all binary expressions.
 */
abstract class EAbstractBinaryExpression : EExpression(), ExpressionContainer {

    abstract var left: EExpression
    abstract var right: EExpression

    override fun acceptChildren(visitor: TreeVisitor) {
        left.accept(visitor)
        right.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return when (oldExpression) {
            left -> {
                left = newExpression
                true
            }
            right -> {
                right = newExpression
                true
            }
            else -> false
        }
    }
}
