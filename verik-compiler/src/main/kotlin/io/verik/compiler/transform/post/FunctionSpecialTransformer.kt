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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EDelayExpression
import io.verik.compiler.ast.element.sv.EEventControlExpression
import io.verik.compiler.ast.element.sv.EEventExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object FunctionSpecialTransformer : PostTransformerStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(FunctionReferenceVisitor)
    }

    object FunctionReferenceVisitor : TreeVisitor() {

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            val newExpression = when (callExpression.reference) {
                Core.Vk.POSEDGE_BOOLEAN -> {
                    EEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0],
                        EdgeType.POSEDGE
                    )
                }
                Core.Vk.NEGEDGE_BOOLEAN -> {
                    EEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0],
                        EdgeType.NEGEDGE
                    )
                }
                Core.Vk.WAIT_EVENT -> {
                    EEventControlExpression(callExpression.location, callExpression.valueArguments[0])
                }
                Core.Vk.DELAY_INT -> {
                    EDelayExpression(callExpression.location, callExpression.valueArguments[0])
                }
                Core.Vk.ON_EVENT_FUNCTION -> {
                    m.error("On expression used out of context", callExpression)
                    return
                }
                else -> null
            }
            if (newExpression != null)
                callExpression.replace(newExpression)
        }
    }
}
