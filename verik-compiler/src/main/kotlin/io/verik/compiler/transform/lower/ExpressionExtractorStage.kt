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
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.sv.EConstantPartSelectExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvArrayAccessExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.ExpressionType
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ExpressionExtractor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ExpressionExtractorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val expressionExtractor = ExpressionExtractor()
        val expressionExtractorVisitor = ExpressionExtractorVisitor(expressionExtractor)
        projectContext.project.accept(expressionExtractorVisitor)
        expressionExtractor.flush()
    }

    private class ExpressionExtractorVisitor(
        private val expressionExtractor: ExpressionExtractor
    ) : TreeVisitor() {

        override fun visitConstantPartSelectExpression(constantPartSelectExpression: EConstantPartSelectExpression) {
            super.visitConstantPartSelectExpression(constantPartSelectExpression)
            val array = constantPartSelectExpression.array
            if (array is EReferenceExpression ||
                array is EConstantPartSelectExpression ||
                array is ESvArrayAccessExpression
            ) return
            val arrayReplacement = ExpressionCopier.shallowCopy(array)
            val (referenceExpression, propertyStatement) =
                getReferenceExpressionAndPropertyStatement(arrayReplacement)
            expressionExtractor.extract(array, referenceExpression, listOf(propertyStatement))
        }

        override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
            super.visitStreamingExpression(streamingExpression)
            if (streamingExpression.getExpressionType() == ExpressionType.INDIRECT_TYPED_SUBEXPRESSION) {
                val streamingExpressionReplacement = ExpressionCopier.shallowCopy(streamingExpression)
                val (referenceExpression, propertyStatement) =
                    getReferenceExpressionAndPropertyStatement(streamingExpressionReplacement)
                expressionExtractor.extract(streamingExpression, referenceExpression, listOf(propertyStatement))
            }
        }

        private fun getReferenceExpressionAndPropertyStatement(
            expression: EExpression
        ): Pair<EReferenceExpression, EPropertyStatement> {
            val property = ESvProperty.getTemporary(
                expression.location,
                expression.type.copy(),
                expression,
                false
            )
            val referenceExpression = EReferenceExpression(
                expression.location,
                property.type.copy(),
                property,
                null
            )
            val propertyStatement = EPropertyStatement(
                expression.location,
                property
            )
            return Pair(referenceExpression, propertyStatement)
        }
    }
}
