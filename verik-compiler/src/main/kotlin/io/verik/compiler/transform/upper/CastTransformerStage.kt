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

import io.verik.compiler.ast.element.common.EIfExpression
import io.verik.compiler.ast.element.common.EPropertyStatement
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.kt.EAsExpression
import io.verik.compiler.ast.element.kt.EIsExpression
import io.verik.compiler.ast.element.kt.EKtBlockExpression
import io.verik.compiler.ast.element.kt.EKtCallExpression
import io.verik.compiler.ast.element.sv.EStringExpression
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ExpressionExtractor
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.Target

object CastTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val expressionExtractor = ExpressionExtractor()
        val castTransformerVisitor = CastTransformerVisitor(expressionExtractor)
        projectContext.project.accept(castTransformerVisitor)
        expressionExtractor.flush()
    }

    private class CastTransformerVisitor(
        private val expressionExtractor: ExpressionExtractor
    ) : TreeVisitor() {

        override fun visitIsExpression(isExpression: EIsExpression) {
            super.visitIsExpression(isExpression)
            val referenceExpression = EReferenceExpression(
                isExpression.location,
                isExpression.property.type.copy(),
                isExpression.property,
                null
            )
            val callExpression = EKtCallExpression(
                isExpression.location,
                Core.Kt.C_Boolean.toType(),
                Target.F_cast,
                null,
                arrayListOf(referenceExpression, isExpression.expression),
                ArrayList()
            )
            val propertyStatement = EPropertyStatement(isExpression.location, isExpression.property)
            if (isExpression.isNegated) {
                val negatedCallExpression = EKtCallExpression(
                    isExpression.location,
                    Core.Kt.C_Boolean.toType(),
                    Core.Kt.Boolean.F_not,
                    callExpression,
                    ArrayList(),
                    ArrayList()
                )
                expressionExtractor.extract(isExpression, negatedCallExpression, listOf(propertyStatement))
            } else {
                expressionExtractor.extract(isExpression, callExpression, listOf(propertyStatement))
            }
        }

        override fun visitAsExpression(asExpression: EAsExpression) {
            super.visitAsExpression(asExpression)
            val property = ESvProperty.getTemporary(
                location = asExpression.location,
                type = asExpression.type.copy(),
                initializer = null,
                isMutable = false
            )
            val referenceExpression = EReferenceExpression(
                property.location,
                property.type.copy(),
                property,
                null
            )
            val propertyStatement = EPropertyStatement(property.location, property)
            val castCallExpression = EKtCallExpression(
                asExpression.location,
                Core.Kt.C_Boolean.toType(),
                Target.F_cast,
                null,
                arrayListOf(referenceExpression, asExpression.expression),
                ArrayList()
            )
            val negatedCallExpression = EKtCallExpression(
                asExpression.location,
                Core.Kt.C_Boolean.toType(),
                Core.Kt.Boolean.F_not,
                castCallExpression,
                ArrayList(),
                ArrayList()
            )
            val stringExpression = EStringExpression(
                asExpression.location,
                "Failed to cast from ${asExpression.expression.type} to ${asExpression.type}"
            )
            val fatalCallExpression = EKtCallExpression(
                asExpression.location,
                Core.Kt.C_Nothing.toType(),
                Core.Vk.F_fatal_String,
                null,
                arrayListOf(stringExpression),
                ArrayList()
            )
            val ifExpression = EIfExpression(
                asExpression.location,
                Core.Kt.C_Unit.toType(),
                negatedCallExpression,
                EKtBlockExpression.wrap(fatalCallExpression),
                null
            )
            expressionExtractor.extract(
                asExpression,
                ExpressionCopier.copy(referenceExpression),
                listOf(propertyStatement, ifExpression)
            )
        }
    }
}
