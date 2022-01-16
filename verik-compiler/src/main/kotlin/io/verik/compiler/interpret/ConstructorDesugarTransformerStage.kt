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

import io.verik.compiler.ast.element.common.EBlockExpression
import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EProperty
import io.verik.compiler.ast.element.common.EReferenceExpression
import io.verik.compiler.ast.element.common.EThisExpression
import io.verik.compiler.ast.element.kt.EKtBinaryExpression
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.element.kt.EKtConstructor
import io.verik.compiler.ast.element.kt.EKtValueParameter
import io.verik.compiler.ast.element.kt.EPrimaryConstructor
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ExpressionCopier
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object ConstructorDesugarTransformerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val constructorDesugarTransformerVisitor = ConstructorDesugarTransformerVisitor(referenceUpdater)
        projectContext.project.accept(constructorDesugarTransformerVisitor)
        referenceUpdater.flush()
    }

    private class ConstructorDesugarTransformerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(`class`: EKtClass) {
            super.visitKtClass(`class`)
            val primaryConstructor = `class`.primaryConstructor
            if (primaryConstructor != null) {
                val declarations = ArrayList<EDeclaration>()
                val properties = desugarValueParameterProperties(primaryConstructor.valueParameters)
                declarations.addAll(properties.filterNotNull())

                val body = getPrimaryConstructorBody(primaryConstructor, properties)
                val superTypeCallExpression = `class`.superTypeCallExpression?.let { ExpressionCopier.deepCopy(it) }
                val constructor = EKtConstructor(
                    primaryConstructor.location,
                    primaryConstructor.name,
                    primaryConstructor.type,
                    body,
                    primaryConstructor.valueParameters,
                    primaryConstructor.typeParameters,
                    superTypeCallExpression
                )
                declarations.add(constructor)
                `class`.primaryConstructor = null
                referenceUpdater.update(primaryConstructor, constructor)

                declarations.forEach { it.parent = `class` }
                `class`.declarations.addAll(0, declarations)
            }
        }

        private fun desugarValueParameterProperties(valueParameters: List<EKtValueParameter>): List<EProperty?> {
            return valueParameters.map {
                if (it.isPrimaryConstructorProperty) {
                    val isMutable = it.isMutable
                    it.isPrimaryConstructorProperty = false
                    it.isMutable = false
                    val property = EProperty.named(
                        location = it.location,
                        name = it.name,
                        type = it.type.copy(),
                        initializer = null,
                        isMutable = isMutable
                    )
                    referenceUpdater.update(it, property)
                    property
                } else {
                    null
                }
            }
        }

        private fun getPrimaryConstructorBody(
            primaryConstructor: EPrimaryConstructor,
            properties: List<EProperty?>
        ): EBlockExpression {
            val statements = ArrayList<EExpression>()
            primaryConstructor.valueParameters.zip(properties).forEach { (valueParameter, property) ->
                if (property != null) {
                    val thisExpression = EThisExpression(valueParameter.location, primaryConstructor.type.copy())
                    val propertyReferenceExpression = EReferenceExpression(
                        valueParameter.location,
                        valueParameter.type.copy(),
                        property,
                        thisExpression
                    )
                    val valueParameterReferenceExpression = EReferenceExpression(
                        valueParameter.location,
                        valueParameter.type.copy(),
                        valueParameter,
                        null
                    )
                    statements.add(
                        EKtBinaryExpression(
                            valueParameter.location,
                            Core.Kt.C_Unit.toType(),
                            propertyReferenceExpression,
                            valueParameterReferenceExpression,
                            KtBinaryOperatorKind.EQ
                        )
                    )
                }
            }
            return EBlockExpression(
                primaryConstructor.location,
                primaryConstructor.location,
                Core.Kt.C_Unit.toType(),
                statements
            )
        }
    }
}
