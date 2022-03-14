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

import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ECluster
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ClusterInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val clusterInterpreterVisitor = ClusterInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(clusterInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ClusterInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.type.reference == Core.Vk.C_Cluster) {
                val initializer = property.initializer
                if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cluster_Int_Function) {
                    interpretCluster(property, initializer)
                } else {
                    Messages.EXPECTED_CLUSTER_EXPRESSION.on(property)
                }
            }
        }

        private fun interpretCluster(property: EProperty, expression: ECallExpression) {
            val functionLiteralExpression = expression.valueArguments[1] as EFunctionLiteralExpression
            if (functionLiteralExpression.body.statements.size != 1) {
                Messages.INVALID_CLUSTER_INITIALIZER.on(functionLiteralExpression)
                return
            }
            val initializer = functionLiteralExpression.body.statements[0]
            val initializerProperty = EProperty(
                location = property.location,
                endLocation = property.endLocation,
                name = property.name,
                type = initializer.type.copy(),
                annotationEntries = property.annotationEntries,
                documentationLines = property.documentationLines,
                initializer = initializer,
                isMutable = false,
                isStatic = false
            )
            val valueParameter = functionLiteralExpression.valueParameters[0]
            val size = expression.type.arguments[0].asCardinalValue(expression)
            val properties = copyProperties(initializerProperty, valueParameter, size)
            val cluster = ECluster(
                property.location,
                property.endLocation,
                property.name,
                properties
            )
            referenceUpdater.replace(property, cluster)
        }

        private fun copyProperties(
            property: EProperty,
            valueParameter: EAbstractValueParameter,
            size: Int
        ): ArrayList<EDeclaration> {
            val properties = ArrayList<EDeclaration>()
            for (index in 0 until size) {
                val copiedInitializer = ExpressionCopier.deepCopy(property.initializer!!)
                val blockExpression = EBlockExpression.wrap(copiedInitializer)
                val valueParameterSubstitutorVisitor = ValueParameterSubstitutorVisitor(valueParameter, index)
                blockExpression.accept(valueParameterSubstitutorVisitor)
                val copiedProperty = EProperty(
                    location = property.location,
                    endLocation = property.endLocation,
                    name = "${property.name}_$index",
                    type = property.type.copy(),
                    annotationEntries = property.annotationEntries,
                    documentationLines = if (index == 0) property.documentationLines else null,
                    initializer = blockExpression.statements[0],
                    isMutable = false,
                    isStatic = false
                )
                properties.add(copiedProperty)
            }
            return properties
        }

        private class ValueParameterSubstitutorVisitor(
            private val valueParameter: EAbstractValueParameter,
            private val index: Int
        ) : TreeVisitor() {

            override fun visitReferenceExpression(referenceExpression: EReferenceExpression) {
                super.visitReferenceExpression(referenceExpression)
                if (referenceExpression.reference == valueParameter) {
                    val constantExpression = ConstantBuilder.buildInt(referenceExpression, index)
                    referenceExpression.replace(constantExpression)
                }
            }
        }
    }
}
