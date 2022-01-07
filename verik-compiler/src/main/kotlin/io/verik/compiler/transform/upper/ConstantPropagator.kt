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

package io.verik.compiler.transform.upper

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreFunctionDeclaration

object ConstantPropagator {

    fun expandExpression(expression: EExpression): EExpression {
        return expression
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

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
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
                Core.Vk.F_u_Boolean,
                Core.Vk.F_u_Int,
                Core.Vk.F_u_String,
                Core.Vk.F_u_Sbit
            )
        }
    }
}
