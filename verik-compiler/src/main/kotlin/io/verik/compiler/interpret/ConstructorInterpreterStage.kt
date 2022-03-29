/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.ast.property.ValueParameterKind
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

/**
 * Stage that interprets SystemVerilog constructors from Kotlin secondary constructors.
 */
object ConstructorInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val constructorInterpreterVisitor = ConstructorInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(constructorInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class ConstructorInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitSecondaryConstructor(secondaryConstructor: ESecondaryConstructor) {
            super.visitSecondaryConstructor(secondaryConstructor)
            val body = interpretBody(secondaryConstructor)
            val valueParameters = ArrayList<ESvValueParameter>()
            secondaryConstructor.valueParameters.forEach {
                val valueParameter = ESvValueParameter(
                    location = it.location,
                    name = it.name,
                    type = it.type,
                    annotationEntries = it.annotationEntries,
                    expression = it.expression,
                    kind = ValueParameterKind.INPUT
                )
                valueParameters.add(valueParameter)
                referenceUpdater.update(it, valueParameter)
            }
            val constructor = ESvConstructor(
                location = secondaryConstructor.location,
                type = secondaryConstructor.type,
                annotationEntries = secondaryConstructor.annotationEntries,
                documentationLines = secondaryConstructor.documentationLines,
                body = body,
                valueParameters = ArrayList(valueParameters)
            )
            referenceUpdater.replace(secondaryConstructor, constructor)
        }

        private fun interpretBody(secondaryConstructor: ESecondaryConstructor): EBlockExpression {
            val body = secondaryConstructor.body
            val superTypeCallExpression = secondaryConstructor.superTypeCallExpression
            if (superTypeCallExpression != null) {
                val reference = superTypeCallExpression.reference
                if (reference is ESecondaryConstructor) {
                    val superExpression = ESuperExpression(
                        secondaryConstructor.location,
                        reference.type.copy()
                    )
                    val callExpression = ECallExpression(
                        superTypeCallExpression.location,
                        Core.Kt.C_Unit.toType(),
                        reference,
                        superExpression,
                        false,
                        superTypeCallExpression.valueArguments,
                        ArrayList()
                    )
                    callExpression.parent = body
                    body.statements.add(0, callExpression)
                }
            }
            return body
        }
    }
}
