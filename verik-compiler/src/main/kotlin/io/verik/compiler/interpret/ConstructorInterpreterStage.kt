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

import io.verik.compiler.ast.element.declaration.kt.ESecondaryConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvConstructor
import io.verik.compiler.ast.element.declaration.sv.ESvValueParameter
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.ESuperExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

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
                    isInput = true
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
