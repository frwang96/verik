/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.declaration.sv

import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.common.cast
import io.verik.compiler.ast.element.declaration.common.EAbstractProperty
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.replaceIfContains

abstract class EAbstractComponentInstantiation : EAbstractProperty(), ExpressionContainer {

    abstract val valueArguments: ArrayList<EExpression>

    fun getPorts(): List<EPort> {
        return type.reference.cast<EAbstractComponent>(this).ports
    }

    override fun acceptChildren(visitor: TreeVisitor) {
        valueArguments.forEach { it.accept(visitor) }
    }

    override fun replaceChild(oldExpression: EExpression, newExpression: EExpression): Boolean {
        newExpression.parent = this
        return valueArguments.replaceIfContains(oldExpression, newExpression)
    }
}
