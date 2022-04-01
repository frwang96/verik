/*
 * SPDX-License-Identifier: Apache-2.0
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

/**
 * Stage that interprets SystemVerilog properties and constraints from Kotlin properties.
 */
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
            if (callExpression !is ECallExpression || callExpression.reference != Core.Vk.F_c_Boolean) return null
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
