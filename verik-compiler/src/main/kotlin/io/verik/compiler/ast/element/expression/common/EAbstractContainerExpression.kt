/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.common.TreeVisitor

/**
 * Abstract expression that contains another [expression].
 */
abstract class EAbstractContainerExpression : EExpression(), ExpressionContainer {

    abstract var expression: EExpression

    override fun acceptChildren(visitor: TreeVisitor) {
        expression.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
