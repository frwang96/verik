/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.common.TreeVisitor

/**
 * Base class for all array access expressions.
 */
abstract class EAbstractArrayAccessExpression : EExpression(), ExpressionContainer {

    abstract var array: EExpression

    override fun acceptChildren(visitor: TreeVisitor) {
        array.accept(visitor)
    }
}
