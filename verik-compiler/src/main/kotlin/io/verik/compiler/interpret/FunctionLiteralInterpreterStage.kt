/*
 * SPDX-License-Identifier: Apache-2.0
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
