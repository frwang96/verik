/*
 * Copyright (c) 2021 Francis Wang
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

package io.verik.compiler.check.mid

import io.verik.compiler.ast.element.common.ECallExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

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
