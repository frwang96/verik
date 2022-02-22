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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EIfExpression
import io.verik.compiler.ast.element.expression.common.EPropertyStatement
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EAsExpression
import io.verik.compiler.ast.element.expression.kt.EIsExpression
import io.verik.compiler.ast.element.expression.sv.EStringExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.target.common.Target

object CastTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(CastTransformerVisitor)
    }

    private object CastTransformerVisitor : TreeVisitor() {

        override fun visitIsExpression(isExpression: EIsExpression) {
            super.visitIsExpression(isExpression)
            val referenceExpression = EReferenceExpression.of(isExpression.property)
            val callExpression = ECallExpression(
                isExpression.location,
                Core.Kt.C_Boolean.toType(),
                Target.F_cast,
                null,
                false,
                arrayListOf(referenceExpression, isExpression.expression),
                ArrayList()
            )
            val propertyStatement = EPropertyStatement(isExpression.location, isExpression.property)
            if (isExpression.isNegated) {
                val negatedCallExpression = ECallExpression(
                    isExpression.location,
                    Core.Kt.C_Boolean.toType(),
                    Core.Kt.Boolean.F_not,
                    callExpression,
                    false,
                    ArrayList(),
                    ArrayList()
                )
                EBlockExpression.extract(isExpression, listOf(propertyStatement, negatedCallExpression))
            } else {
                EBlockExpression.extract(isExpression, listOf(propertyStatement, callExpression))
            }
        }

        override fun visitAsExpression(asExpression: EAsExpression) {
            super.visitAsExpression(asExpression)
            val property = EProperty.temporary(
                location = asExpression.location,
                type = asExpression.type.copy(),
                initializer = null,
                isMutable = false
            )
            val referenceExpression = EReferenceExpression.of(property)
            val propertyStatement = EPropertyStatement(property.location, property)
            val castCallExpression = ECallExpression(
                asExpression.location,
                Core.Kt.C_Boolean.toType(),
                Target.F_cast,
                null,
                false,
                arrayListOf(referenceExpression, asExpression.expression),
                ArrayList()
            )
            val negatedCallExpression = ECallExpression(
                asExpression.location,
                Core.Kt.C_Boolean.toType(),
                Core.Kt.Boolean.F_not,
                castCallExpression,
                false,
                ArrayList(),
                ArrayList()
            )
            val stringExpression = EStringExpression(
                asExpression.location,
                "Failed to cast from ${asExpression.expression.type} to ${asExpression.type}"
            )
            val fatalCallExpression = ECallExpression(
                asExpression.location,
                Core.Kt.C_Nothing.toType(),
                Core.Vk.F_fatal_String,
                null,
                false,
                arrayListOf(stringExpression),
                ArrayList()
            )
            val ifExpression = EIfExpression(
                asExpression.location,
                Core.Kt.C_Unit.toType(),
                negatedCallExpression,
                EBlockExpression.wrap(fatalCallExpression),
                null
            )
            val extractedExpressions = listOf(
                propertyStatement,
                ifExpression,
                ExpressionCopier.deepCopy(referenceExpression)
            )
            EBlockExpression.extract(asExpression, extractedExpressions)
        }
    }
}
