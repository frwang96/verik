/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.element.common

import io.verik.compiler.ast.common.DeclarationContainer
import io.verik.compiler.ast.common.ExpressionContainer
import io.verik.compiler.ast.element.declaration.common.EAbstractClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.common.Visitor
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation

abstract class EElement {

    abstract val location: SourceLocation

    var parent: EElement? = null

    fun parentNotNull(): EElement {
        return parent
            ?: Messages.INTERNAL_ERROR.on(this, "Parent element of $this should not be null")
    }

    inline fun <reified E : EElement> cast(): E {
        return when (this) {
            is E -> this
            else -> {
                Messages.INTERNAL_ERROR.on(this, "Could not cast element: Expected ${E::class.simpleName} actual $this")
            }
        }
    }

    fun replaceChildAsExpressionContainer(oldExpression: EExpression, newExpression: EExpression) {
        if (this is ExpressionContainer) {
            if (!this.replaceChild(oldExpression, newExpression))
                Messages.INTERNAL_ERROR.on(oldExpression, "Could not find $oldExpression in $this")
        } else {
            Messages.INTERNAL_ERROR.on(oldExpression, "Could not replace $oldExpression in $this")
        }
    }

    fun replaceChildAsDeclarationContainer(oldDeclaration: EDeclaration, newDeclaration: EDeclaration) {
        if (this is DeclarationContainer) {
            if (!this.replaceChild(oldDeclaration, newDeclaration))
                Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not find $oldDeclaration in $this")
        } else {
            Messages.INTERNAL_ERROR.on(oldDeclaration, "Could not replace $oldDeclaration in $this")
        }
    }

    fun getParentPackage(): EPackage {
        return when (val parent = parent) {
            is EPackage -> parent
            null -> Messages.INTERNAL_ERROR.on(this, "Parent package not found")
            else -> parent.getParentPackage()
        }
    }

    fun getParentClassOrNull(): EAbstractClass? {
        return when (val parent = parent) {
            is EAbstractClass -> parent
            null -> null
            else -> parent.getParentClassOrNull()
        }
    }

    fun isImported(): Boolean {
        return getParentPackage().kind.isImported()
    }

    abstract fun accept(visitor: Visitor)

    abstract fun acceptChildren(visitor: TreeVisitor)

    override fun toString(): String {
        return "${this::class.simpleName}"
    }
}
