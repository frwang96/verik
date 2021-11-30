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

import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.sv.EStreamingExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.ExpressionType
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext

object SubexpressionExtractorStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val subexpressionExtractor = SubexpressionExtractor()
        val subexpressionExtractorVisitor = SubexpressionExtractorVisitor(subexpressionExtractor)
        projectContext.project.accept(subexpressionExtractorVisitor)
        subexpressionExtractor.flush()
    }

    private class SubexpressionExtractorVisitor(
        private val subexpressionExtractor: SubexpressionExtractor
    ) : TreeVisitor() {

        override fun visitStreamingExpression(streamingExpression: EStreamingExpression) {
            super.visitStreamingExpression(streamingExpression)
            if (streamingExpression.getExpressionType() == ExpressionType.INDIRECT_TYPED_SUBEXPRESSION) {
                val streamingExpressionReplacement = EStreamingExpression(
                    streamingExpression.location,
                    streamingExpression.type.copy(),
                    streamingExpression.expression
                )
                val property = ESvProperty(
                    streamingExpression.location,
                    "<tmp>",
                    streamingExpression.type.copy(),
                    streamingExpressionReplacement,
                    isMutable = false,
                    isStatic = false
                )
                val referenceExpression = EReferenceExpression(
                    streamingExpression.location,
                    property.type.copy(),
                    property,
                    null
                )
                val propertyStatement = EPropertyStatement(
                    streamingExpression.location,
                    property
                )
                subexpressionExtractor.extract(streamingExpression, referenceExpression, listOf(propertyStatement))
            }
        }
    }
}
