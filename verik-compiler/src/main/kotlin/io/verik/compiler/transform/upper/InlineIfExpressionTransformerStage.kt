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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.sv.EInlineIfExpression
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object InlineIfExpressionTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(InlineIfExpressionTransformerVisitor)
    }

    private object InlineIfExpressionTransformerVisitor : TreeVisitor() {

        private fun reduceExpression(expression: EBlockExpression?): EExpression? {
            return if (expression?.statements?.size == 1) {
                expression.statements.first()
            } else null
        }

        override fun visitIfExpression(ifExpression: EIfExpression) {
            super.visitIfExpression(ifExpression)
            if (ifExpression.getExpressionType().isSubexpression()) {
                val reducedThenExpression = reduceExpression(ifExpression.thenExpression)
                val reducedElseExpression = reduceExpression(ifExpression.elseExpression)
                if (reducedThenExpression != null && reducedElseExpression != null) {
                    val inlineIfExpression = EInlineIfExpression(
                        ifExpression.location,
                        ifExpression.type,
                        ifExpression.condition,
                        reducedThenExpression,
                        reducedElseExpression
                    )
                    ifExpression.replace(inlineIfExpression)
                }
            }
        }
    }
}
