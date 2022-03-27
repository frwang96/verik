/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor

abstract class EAbstractValueParameter : EAbstractProperty(), ExpressionContainer {

    abstract var expression: EExpression?

    override var documentationLines: List<String>? = null

    override fun acceptChildren(visitor: TreeVisitor) {
        expression?.accept(visitor)
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return if (expression == oldExpression) {
            expression = newExpression
            true
        } else false
    }
}
