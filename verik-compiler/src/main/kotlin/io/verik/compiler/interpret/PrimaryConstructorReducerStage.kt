/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.EKtClass
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.element.declaration.kt.EPrimaryConstructor
import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.ast.element.expression.common.EThisExpression
import io.verik.compiler.ast.element.expression.kt.EKtBinaryExpression
import io.verik.compiler.ast.property.KtBinaryOperatorKind
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that reduces Kotlin primary constructors to Kotlin secondary constructors.
 */
object PrimaryConstructorReducerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val primaryConstructorReducerVisitor = PrimaryConstructorReducerVisitor(referenceUpdater)
        projectContext.project.accept(primaryConstructorReducerVisitor)
        referenceUpdater.flush()
    }

    private class PrimaryConstructorReducerVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitKtClass(cls: EKtClass) {
            super.visitKtClass(cls)
            val primaryConstructor = cls.primaryConstructor
            if (primaryConstructor != null) {
                val declarations = ArrayList<EDeclaration>()
                val properties = getProperties(primaryConstructor.valueParameters)
                declarations.addAll(properties.filterNotNull())

                val body = getBody(primaryConstructor, properties)
                val superTypeCallExpression = primaryConstructor.superTypeCallExpression
                val secondaryConstructor = ESecondaryConstructor(
                    location = primaryConstructor.location,
                    name = primaryConstructor.name,
                    type = primaryConstructor.type,
                    documentationLines = null,
                    body = body,
                    valueParameters = primaryConstructor.valueParameters,
                    superTypeCallExpression = superTypeCallExpression
                )
                declarations.add(secondaryConstructor)
                cls.primaryConstructor = null
                referenceUpdater.update(primaryConstructor, secondaryConstructor)

                declarations.forEach { it.parent = cls }
                cls.declarations.addAll(0, declarations)
            }
        }

        private fun getProperties(valueParameters: List<EKtValueParameter>): List<EProperty?> {
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

        private fun getBody(primaryConstructor: EPrimaryConstructor, properties: List<EProperty?>): EBlockExpression {
            val statements = ArrayList<EExpression>()
            primaryConstructor.valueParameters.zip(properties).forEach { (valueParameter, property) ->
                if (property != null) {
                    val thisExpression = EThisExpression(valueParameter.location, primaryConstructor.type.copy())
                    val propertyReferenceExpression = EReferenceExpression(
                        valueParameter.location,
                        valueParameter.type.copy(),
                        property,
                        thisExpression,
                        false
                    )
                    val valueParameterReferenceExpression = EReferenceExpression.of(
                        valueParameter.location,
                        valueParameter
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
