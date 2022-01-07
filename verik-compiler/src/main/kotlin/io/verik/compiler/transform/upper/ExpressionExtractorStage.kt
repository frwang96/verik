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

import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.ExpressionType
import io.verik.compiler.common.ExpressionExtractor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
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

        override fun visitKtCallExpression(callExpression: EKtCallExpression) {
            super.visitKtCallExpression(callExpression)
            if (callExpression.reference == Core.Vk.Ubit.F_reverse &&
                callExpression.getExpressionType() == ExpressionType.INDIRECT_TYPED_SUBEXPRESSION
            ) {
                val callExpressionReplacement = EKtCallExpression(
                    callExpression.location,
                    callExpression.type,
                    callExpression.reference,
                    callExpression.receiver,
                    callExpression.valueArguments,
                    callExpression.typeArguments
                )
                val property = ESvProperty.getTemporary(
                    location = callExpression.location,
                    type = callExpression.type.copy(),
                    initializer = callExpressionReplacement,
                    isMutable = false
                )
                val referenceExpression = EReferenceExpression(
                    callExpression.location,
                    property.type.copy(),
                    property,
                    null
                )
                val propertyStatement = EPropertyStatement(
                    callExpression.location,
                    property
                )
                expressionExtractor.extract(callExpression, referenceExpression, listOf(propertyStatement))
            }
        }
    }
}
