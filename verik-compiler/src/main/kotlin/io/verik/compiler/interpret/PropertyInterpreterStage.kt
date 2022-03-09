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
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.sv.EConstraint
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.expression.common.EBlockExpression
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object PropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val propertyInterpreterVisitor = PropertyInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(propertyInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class PropertyInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretConstraint(property: EProperty): EConstraint? {
            val callExpression = property.initializer
            if (callExpression !is ECallExpression ||
                callExpression.reference !in listOf(Core.Vk.F_c_Boolean, Core.Vk.F_c_String)
            ) return null
            val body = EBlockExpression(
                location = property.location,
                endLocation = property.endLocation,
                type = Core.Kt.C_Unit.toType(),
                statements = callExpression.valueArguments
            )
            return EConstraint(
                location = property.location,
                name = property.name,
                annotationEntries = property.annotationEntries,
                documentationLines = property.documentationLines,
                body = body
            )
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            if (property.hasAnnotationEntry(AnnotationEntries.CONS)) {
                val interpretedProperty = interpretConstraint(property)
                if (interpretedProperty != null) {
                    referenceUpdater.replace(property, interpretedProperty)
                }
            } else {
                val parent = property.parent
                property.isStatic = (parent is ESvClass && parent.isObject) || parent is ECompanionObject
            }
        }
    }
}
