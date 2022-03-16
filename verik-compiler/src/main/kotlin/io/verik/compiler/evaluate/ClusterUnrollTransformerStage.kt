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

package io.verik.compiler.evaluate

import io.verik.compiler.ast.common.Declaration
import io.verik.compiler.ast.element.declaration.common.EAbstractValueParameter
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtAbstractFunction
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.kt.EFunctionLiteralExpression
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.constant.ConstantBuilder
import io.verik.compiler.constant.ConstantNormalizer
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object ClusterUnrollTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val clusterIndexerVisitor = ClusterIndexerVisitor()
        projectContext.project.accept(clusterIndexerVisitor)
        val clusterCallExpressionTransformerVisitor = ClusterCallExpressionTransformerVisitor(
            clusterIndexerVisitor.propertyClusterMap,
            clusterIndexerVisitor.valueParameterClusterMap
        )
        projectContext.project.accept(clusterCallExpressionTransformerVisitor)
        val clusterDeclarationTransformerVisitor = ClusterDeclarationTransformerVisitor(
            clusterIndexerVisitor.propertyClusterMap,
            clusterIndexerVisitor.valueParameterClusterMap
        )
        projectContext.project.accept(clusterDeclarationTransformerVisitor)
    }

    private class ClusterIndexerVisitor : TreeVisitor() {

        val propertyClusterMap = HashMap<EProperty, List<EProperty>>()
        val valueParameterClusterMap = HashMap<EKtValueParameter, List<EKtValueParameter>>()

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.type.reference == Core.Vk.C_Cluster) {
                val initializer = property.initializer
                if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cluster_Int_Function) {
                    indexProperty(property, initializer)
                } else {
                    Messages.EXPECTED_CLUSTER_EXPRESSION.on(property)
                }
            }
        }

        override fun visitKtAbstractFunction(abstractFunction: EKtAbstractFunction) {
            super.visitKtAbstractFunction(abstractFunction)
            abstractFunction.valueParameters.forEach {
                if (it.type.reference == Core.Vk.C_Cluster) {
                    indexValueParameter(it)
                }
            }
        }

        private fun indexProperty(property: EProperty, expression: ECallExpression) {
            val functionLiteralExpression = expression.valueArguments[1] as EFunctionLiteralExpression
            if (functionLiteralExpression.body.statements.size != 1) {
                Messages.INVALID_CLUSTER_INITIALIZER.on(functionLiteralExpression)
                return
            }
            val size = expression.type.arguments[0].asCardinalValue(expression)
            val initializer = functionLiteralExpression.body.statements[0]
            val valueParameter = functionLiteralExpression.valueParameters[0]
            val copiedProperties = ArrayList<EProperty>()
            for (index in 0 until size) {
                val copiedInitializer = ExpressionCopier.deepCopy(initializer)
                val blockExpression = EBlockExpression.wrap(copiedInitializer)
                val valueParameterSubstitutorVisitor = ValueParameterSubstitutorVisitor(valueParameter, index)
                blockExpression.accept(valueParameterSubstitutorVisitor)
                val copiedProperty = EProperty(
                    location = property.location,
                    endLocation = property.endLocation,
                    name = "${property.name}_$index",
                    type = initializer.type.copy(),
                    annotationEntries = property.annotationEntries,
                    documentationLines = if (index == 0) property.documentationLines else null,
                    initializer = blockExpression.statements[0],
                    isMutable = false,
                    isStatic = false
                )
                copiedProperties.add(copiedProperty)
            }
            propertyClusterMap[property] = copiedProperties
        }

        private fun indexValueParameter(valueParameter: EKtValueParameter) {
            val size = valueParameter.type.arguments[0].asCardinalValue(valueParameter)
            val type = valueParameter.type.arguments[1]
            val valueParameters = ArrayList<EKtValueParameter>()
            for (index in 0 until size) {
                val copiedValueParameter = EKtValueParameter(
                    location = valueParameter.location,
                    name = "${valueParameter.name}_$index",
                    type = type.copy(),
                    annotationEntries = valueParameter.annotationEntries,
                    expression = null,
                    isPrimaryConstructorProperty = valueParameter.isPrimaryConstructorProperty,
                    isMutable = valueParameter.isMutable
                )
                valueParameters.add(copiedValueParameter)
            }
            valueParameterClusterMap[valueParameter] = valueParameters
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

    private class ClusterCallExpressionTransformerVisitor(
        private val propertyClusterMap: HashMap<EProperty, List<EProperty>>,
        private val valueParameterClusterMap: HashMap<EKtValueParameter, List<EKtValueParameter>>
    ) : TreeVisitor() {

        override fun visitCallExpression(callExpression: ECallExpression) {
            super.visitCallExpression(callExpression)
            val reference = callExpression.reference
            val receiver = callExpression.receiver
            if (reference == Core.Vk.Cluster.F_get_Int && receiver is EReferenceExpression) {
                transformIndex(callExpression, receiver)
            } else if (reference is EKtAbstractFunction) {
                transformValueArguments(callExpression, reference)
            }
        }

        private fun transformIndex(callExpression: ECallExpression, receiver: EReferenceExpression) {
            val declarations = getClusterDeclarations(receiver.reference) ?: return
            val constantExpression = callExpression.valueArguments[0]
            val index = ConstantNormalizer.parseIntOrNull(constantExpression)
            if (index == null) {
                Messages.EXPRESSION_NOT_CONSTANT.on(constantExpression)
            } else if (index < 0 || index >= declarations.size) {
                Messages.CLUSTER_INDEX_INVALID.on(constantExpression, index)
            } else {
                val referenceExpression = EReferenceExpression.of(callExpression.location, declarations[index])
                callExpression.replace(referenceExpression)
            }
        }

        private fun transformValueArguments(callExpression: ECallExpression, function: EKtAbstractFunction) {
            if (function.valueParameters.any { it.type.reference == Core.Vk.C_Cluster }) {
                val valueArguments = ArrayList<EExpression>()
                callExpression.valueArguments.zip(function.valueParameters).forEach { (valueArgument, valueParameter) ->
                    if (valueParameter.type.reference == Core.Vk.C_Cluster) {
                        val referenceExpression = valueArgument as? EReferenceExpression ?: return
                        val declarations = getClusterDeclarations(referenceExpression.reference) ?: return
                        declarations.forEach {
                            valueArguments.add(EReferenceExpression.of(referenceExpression.location, it))
                        }
                    } else {
                        valueArguments.add(valueArgument)
                    }
                }
                valueArguments.forEach { it.parent = callExpression }
                callExpression.valueArguments = valueArguments
            }
        }

        private fun getClusterDeclarations(reference: Declaration): List<EDeclaration>? {
            return propertyClusterMap[reference] ?: valueParameterClusterMap[reference]
        }
    }

    private class ClusterDeclarationTransformerVisitor(
        private val propertyClusterMap: HashMap<EProperty, List<EProperty>>,
        private val valueParameterClusterMap: HashMap<EKtValueParameter, List<EKtValueParameter>>
    ) : TreeVisitor() {

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = transformDeclarations(file, file.declarations)
        }

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            cls.declarations = transformDeclarations(cls, cls.declarations)
        }

        override fun visitKtAbstractFunction(abstractFunction: EKtAbstractFunction) {
            super.visitKtAbstractFunction(abstractFunction)
            val transformedValueParameters = ArrayList<EKtValueParameter>()
            abstractFunction.valueParameters.forEach { valueParameter ->
                val valueParameters = valueParameterClusterMap[valueParameter]
                if (valueParameters != null) {
                    valueParameters.forEach { it.parent = abstractFunction }
                    transformedValueParameters.addAll(valueParameters)
                } else {
                    transformedValueParameters.add(valueParameter)
                }
            }
            abstractFunction.valueParameters = transformedValueParameters
        }

        private fun transformDeclarations(
            parent: EDeclaration,
            declarations: List<EDeclaration>,
        ): ArrayList<EDeclaration> {
            val transformedDeclarations = ArrayList<EDeclaration>()
            declarations.forEach { declaration ->
                val properties = propertyClusterMap[declaration]
                if (properties != null) {
                    properties.forEach { it.parent = parent }
                    transformedDeclarations.addAll(properties)
                } else {
                    transformedDeclarations.add(declaration)
                }
            }
            return transformedDeclarations
        }
    }
}
