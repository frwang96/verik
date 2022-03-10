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

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EGenerateForBlock
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object GenerateForBlockInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val generateForBlockInterpreterVisitor = GenerateForBlockInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(generateForBlockInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class GenerateForBlockInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretGenerateForBlock(property: EProperty, expression: ECallExpression) {
            val functionLiteralExpression = expression.valueArguments[0] as EFunctionLiteralExpression
            if (functionLiteralExpression.body.statements.size != 1) {
                Messages.INVALID_CLUSTER_INITIALIZER.on(functionLiteralExpression)
                return
            }
            val valueParameter = functionLiteralExpression.valueParameters[0]
            val indexProperty = EProperty(
                location = valueParameter.location,
                endLocation = valueParameter.endLocation,
                name = valueParameter.name,
                type = valueParameter.type,
                annotationEntries = valueParameter.annotationEntries,
                documentationLines = valueParameter.documentationLines,
                initializer = null,
                isMutable = true,
                isStatic = false
            )
            val initializer = functionLiteralExpression.body.statements[0]
            val initializerProperty = EProperty(
                location = property.location,
                endLocation = property.endLocation,
                name = "gen",
                type = initializer.type.copy(),
                annotationEntries = property.annotationEntries,
                documentationLines = null,
                initializer = initializer,
                isMutable = true,
                isStatic = false
            )
            val size = expression.typeArguments[0].asCardinalValue(expression)
            val generateForBlock = EGenerateForBlock(
                property.location,
                property.endLocation,
                property.name,
                property.documentationLines,
                indexProperty,
                initializerProperty,
                size
            )
            referenceUpdater.update(valueParameter, indexProperty)
            referenceUpdater.replace(property, generateForBlock)
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.type.reference == Core.Vk.C_Cluster) {
                val initializer = property.initializer
                if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cluster_Function) {
                    interpretGenerateForBlock(property, initializer)
                } else {
                    Messages.EXPECTED_CLUSTER_EXPRESSION.on(property)
                }
            }
        }
    }
}
