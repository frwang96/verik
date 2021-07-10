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

import io.verik.compiler.ast.element.common.CCallExpression
import io.verik.compiler.ast.element.sv.SEventControlExpression
import io.verik.compiler.ast.element.sv.SEventExpression
import io.verik.compiler.ast.property.EdgeType
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.CoreFunction
import io.verik.compiler.main.ProjectContext

object FunctionSpecialTransformer : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.verikFiles.forEach {
            it.accept(FunctionReferenceVisitor)
        }
    }

    object FunctionReferenceVisitor : TreeVisitor() {

        override fun visitCCallExpression(callExpression: CCallExpression) {
            super.visitCCallExpression(callExpression)
            val newExpression = when (callExpression.reference) {
                CoreFunction.Core.POSEDGE_BOOLEAN -> {
                    SEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0].expression,
                        EdgeType.POSEDGE
                    )
                }
                CoreFunction.Core.NEGEDGE_BOOLEAN -> {
                    SEventExpression(
                        callExpression.location,
                        callExpression.valueArguments[0].expression,
                        EdgeType.NEGEDGE
                    )
                }
                CoreFunction.Core.WAIT_EVENT -> {
                    SEventControlExpression(callExpression.location, callExpression.valueArguments[0].expression)
                }
                else -> null
            }
            if (newExpression != null)
                callExpression.replace(newExpression)
        }
    }
}