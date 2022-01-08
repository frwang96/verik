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

package io.verik.compiler.transform.lower

import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object IfExpressionEvaluatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(IfExpressionEvaluatorVisitor)
    }

    private object IfExpressionEvaluatorVisitor : TreeVisitor() {

        override fun visitKtBlockExpression(blockExpression: EKtBlockExpression) {
            super.visitKtBlockExpression(blockExpression)
            if (blockExpression.statements.any { it is EIfExpression }) {
                val statements = ArrayList<EExpression>()
                blockExpression.statements.forEach { statement ->
                    if (statement is EIfExpression) {
                        val evaluatedStatements = evaluateIfExpression(statement)
                        evaluatedStatements.forEach { it.parent = blockExpression }
                        statements.addAll(evaluatedStatements)
                    } else {
                        statements.add(statement)
                    }
                }
                blockExpression.statements = statements
            }
        }

        private fun evaluateIfExpression(ifExpression: EIfExpression): List<EExpression> {
            return when (ConstantNormalizer.parseBooleanOrNull(ifExpression.condition)) {
                true -> ifExpression.thenExpression?.statements ?: listOf()
                false -> ifExpression.elseExpression?.statements ?: listOf()
                else -> listOf(ifExpression)
            }
        }
    }
}
