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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.expression.sv.EStreamingExpression
import io.verik.compiler.ast.element.expression.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.property.ExpressionKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

// TODO move before ForEachUnrollTransformerStage
object ExpressionExtractorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(ExpressionExtractorVisitor)
    }

    private object ExpressionExtractorVisitor : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val isExtractable = callExpression.getExpressionKind() == ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION ||
                callExpression.parent is ECallExpression
            if (isExtractable && callExpression.reference is ESvConstructor) {
                val callExpressionReplacement = ExpressionCopier.shallowCopy(callExpression)
                val (propertyStatement, referenceExpression) =
                    getPropertyStatementAndReferenceExpression(callExpressionReplacement)
                EBlockExpression.extract(callExpression, listOf(propertyStatement, referenceExpression))
            }
        }

        override fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
            super.visitConstantPartSelectExpression(constantPartSelectExpression)
            val array = constantPartSelectExpression.array
            if (array is EReferenceExpression ||
                array is EConstantPartSelectExpression ||
                array is ESvArrayAccessExpression
            ) return
            val arrayReplacement = ExpressionCopier.shallowCopy(array)
            val (propertyStatement, referenceExpression) =
                getPropertyStatementAndReferenceExpression(arrayReplacement)
            EBlockExpression.extract(array, listOf(propertyStatement, referenceExpression))
        }

        override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
            super.visitStreamingExpression(streamingExpression)
            if (streamingExpression.getExpressionKind() == ExpressionKind.INDIRECT_TYPED_SUBEXPRESSION) {
                val streamingExpressionReplacement = ExpressionCopier.shallowCopy(streamingExpression)
                val (propertyStatement, referenceExpression) =
                    getPropertyStatementAndReferenceExpression(streamingExpressionReplacement)
                EBlockExpression.extract(streamingExpression, listOf(propertyStatement, referenceExpression))
            }
        }

        private fun getPropertyStatementAndReferenceExpression(
            expression: EExpression
        ): Pair<EPropertyStatement, EReferenceExpression> {
            val property = EProperty.temporary(
                expression.location,
                expression.type.copy(),
                expression,
                false
            )
            val propertyStatement = EPropertyStatement(
                expression.location,
                property
            )
            val referenceExpression = EReferenceExpression.of(property)
            return Pair(propertyStatement, referenceExpression)
        }
    }
}
