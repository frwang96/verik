/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Stage that checks for violations of the mutability of bit types. Contrary to Kotlin, Verik considers it a violation
 * of mutability if a bit type property declared as val is mutated with the set function.
 */
object ArrayAccessMutabilityChecker : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ArrayAccessMutabilityVisitor)
    }

    private object ArrayAccessMutabilityVisitor : TreeVisitor() {

        private val arrayAccessFunctionDeclarations = listOf(
            Core.Vk.Ubit.F_set_Int_Boolean,
            Core.Vk.Ubit.F_set_Ubit_Boolean,
            Core.Vk.Ubit.F_set_Int_Int_Ubit,
            Core.Vk.Sbit.F_set_Int_Boolean,
            Core.Vk.Sbit.F_set_Ubit_Boolean
        )

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            if (callExpression.reference in arrayAccessFunctionDeclarations) {
                checkReceiver(callExpression.receiver)
            }
        }

        private fun checkReceiver(expression: EExpression?) {
            when (expression) {
                is EReferenceExpression -> {
                    val reference = expression.reference
                    if (reference is EProperty && !reference.isMutable) {
                        Messages.VAL_REASSIGNED.on(expression, reference.name)
                    }
                }
                is ECallExpression -> {
                    checkReceiver(expression.receiver)
                }
            }
        }
    }
}
