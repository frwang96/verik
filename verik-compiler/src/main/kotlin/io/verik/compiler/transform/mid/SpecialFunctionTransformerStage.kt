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

package io.verik.compiler.transform.mid

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object SpecialFunctionTransformerStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionSpecialTransformerVisitor)
    }

    object FunctionSpecialTransformerVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val newExpression = when (val reference = callExpression.reference) {
                Core.Vk.F_NC -> {
                    Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, reference.name)
                    return
                }
                Core.Vk.F_ON_EVENT_FUNCTION -> {
                    Messages.EXPRESSION_OUT_OF_CONTEXT.on(callExpression, reference.name)
                    return
                }
                Core.Vk.F_POSEDGE_BOOLEAN -> {
                    EEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0],
                        EdgeType.POSEDGE
                    )
                }
                Core.Vk.F_NEGEDGE_BOOLEAN -> {
                    EEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0],
                        EdgeType.NEGEDGE
                    )
                }
                Core.Vk.F_WAIT_EVENT -> {
                    EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
                }
                Core.Vk.F_DELAY_INT -> {
                    EDelayExpression(callExpression.location, callExpression.valueArguments[0])
                }
                Core.Vk.Ubit.F_EXT -> {
                    // TODO appropriately cast receiver
                    callExpression.receiver!!
                }
                else -> null
            }
            if (newExpression != null)
                callExpression.replace(newExpression)
        }
    }
}
