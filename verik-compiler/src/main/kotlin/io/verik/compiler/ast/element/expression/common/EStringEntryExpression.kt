/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.expression.common

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.property.ExpressionStringEntry
import io.verik.compiler.ast.property.StringEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor

abstract class EStringEntryExpression : EExpression(), ExpressionContainer {

    abstract val entries: List<StringEntry>

    override fun accept(visitor: Visitor) {
        visitor.visitStringEntryExpression(this)
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        entries.forEach {
            if (it is ExpressionStringEntry)
                it.expression.accept(visitor)
        }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        entries.forEach {
            if (it is ExpressionStringEntry && it.expression == oldExpression) {
                it.expression = newExpression
                return true
            }
        }
        return false
    }
}
