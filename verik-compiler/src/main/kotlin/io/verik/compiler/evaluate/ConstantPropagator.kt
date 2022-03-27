/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EConstantExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreFunctionDeclaration

object ConstantPropagator {

    fun expand(expression: EExpression): EExpression {
        return when (expression) {
            is EKtBinaryExpression -> expandKtBinaryExpression(expression)
            is EReferenceExpression -> expandReferenceExpression(expression)
            is ECallExpression -> expandCallExpression(expression)
            is EConstantExpression -> expression
            else -> expression
        }
    }

    private fun expandKtBinaryExpression(binaryExpression: EKtBinaryExpression): EExpression {
        val left = expand(binaryExpression.left)
        val right = expand(binaryExpression.right)
        return EKtBinaryExpression(
            binaryExpression.location,
            binaryExpression.type,
            left,
            right,
            binaryExpression.kind
        )
    }

    private fun expandReferenceExpression(referenceExpression: EReferenceExpression): EExpression {
        val reference = referenceExpression.reference
        if (reference is EProperty && !reference.isMutable) {
            val initializer = reference.initializer
            if (initializer != null) {
                val copiedInitializer = ExpressionCopier.deepCopy(initializer, referenceExpression.location)
                return expand(copiedInitializer)
            }
        }
        return referenceExpression
    }

    private fun expandCallExpression(callExpression: ECallExpression): EExpression {
        val receiver = callExpression.receiver?.let { expand(it) }
        val valueArguments = callExpression.valueArguments.map { expand(it) }
        return ECallExpression(
            callExpression.location,
            callExpression.type,
            callExpression.reference,
            receiver,
            false,
            ArrayList(valueArguments),
            callExpression.typeArguments
        )
    }

    fun isConstant(expression: EExpression): Boolean {
        val constantCheckerVisitor = ConstantCheckerVisitor()
        expression.accept(constantCheckerVisitor)
        return constantCheckerVisitor.isConstant
    }

    private class ConstantCheckerVisitor : TreeVisitor() {

        var isConstant = true

        override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
            isConstant = false
        }

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            if (reference !is CoreFunctionDeclaration || reference !in constantFunctionDeclarations) {
                isConstant = false
            }
        }

        companion object {

            private val constantFunctionDeclarations: List<CoreFunctionDeclaration> = listOf(
                Core.Vk.F_b,
                Core.Vk.F_i,
                Core.Vk.F_u,
                Core.Vk.F_u_Int,
                Core.Vk.F_u_String,
                Core.Vk.F_u0,
                Core.Vk.F_u1,
                Core.Vk.F_s0,
                Core.Vk.F_s1
            )
        }
    }
}
