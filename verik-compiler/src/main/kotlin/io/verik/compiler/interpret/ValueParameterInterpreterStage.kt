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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.sv.ESvValueParameter
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object ValueParameterInterpreterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val valueParameterInterpreterVisitor = ValueParameterInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(valueParameterInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ValueParameterInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
            super.visitFunctionLiteralExpression(functionLiteralExpression)
            functionLiteralExpression.valueParameters.forEach {
                val oldValueParameter = it.cast<EKtValueParameter>()
                if (oldValueParameter != null) {
                    val newValueParameter = ESvValueParameter(
                        oldValueParameter.location,
                        oldValueParameter.name,
                        oldValueParameter.type
                    )
                    referenceUpdater.replace(oldValueParameter, newValueParameter)
                }
            }
        }
    }
}
