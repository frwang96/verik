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

import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object FunctionLiteralInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val functionLiteralInterpreterVisitor = FunctionLiteralInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(functionLiteralInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class FunctionLiteralInterpreterVisitor(private val referenceUpdater: ReferenceUpdater) : TreeVisitor() {

        override fun visitFunctionLiteralExpression(functionLiteralExpression: EFunctionLiteralExpression) {
            super.visitFunctionLiteralExpression(functionLiteralExpression)
            functionLiteralExpression.valueParameters.forEach {
                val oldValueParameter = it.cast<EKtValueParameter>()
                val newValueParameter = ESvValueParameter(
                    oldValueParameter.location,
                    oldValueParameter.name,
                    oldValueParameter.type,
                    oldValueParameter.annotationEntries,
                    oldValueParameter.expression,
                    ValueParameterKind.INPUT
                )
                referenceUpdater.replace(oldValueParameter, newValueParameter)
            }
        }
    }
}
