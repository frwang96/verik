/*
 * Copyright (c) 2022 Francis Wang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.verik.compiler.evaluate

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EConstantExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
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
