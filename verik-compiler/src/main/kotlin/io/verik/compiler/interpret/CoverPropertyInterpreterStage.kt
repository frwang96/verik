/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.ECoverCross
import io.verik.compiler.ast.element.declaration.sv.ECoverPoint
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.ast.element.expression.common.EReferenceExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object CoverPropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val coverPointInterpreterVisitor = CoverPointInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverPointInterpreterVisitor)
        referenceUpdater.flush()

        val coverCrossInterpreterVisitor = CoverCrossInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverCrossInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class CoverPointInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.COVER) &&
                initializer != null &&
                property.type.reference == Core.Vk.C_CoverPoint
            ) {
                interpretCoverPoint(property, initializer)
            }
        }

        private fun interpretCoverPoint(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cp_Any_String) {
                val binExpressions = initializer.valueArguments.drop(1)
                val coverPoint = ECoverPoint(
                    property.location,
                    property.endLocation,
                    property.name,
                    property.annotationEntries,
                    property.documentationLines,
                    initializer.valueArguments[0],
                    ArrayList(binExpressions)
                )
                referenceUpdater.replace(property, coverPoint)
            }
        }
    }

    private class CoverCrossInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.COVER) &&
                initializer != null &&
                property.type.reference == Core.Vk.C_CoverCross
            ) {
                interpretCoverCross(property, initializer)
            }
        }

        private fun interpretCoverCross(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression) {
                when (initializer.reference) {
                    Core.Vk.F_cc_CoverPoint_CoverPoint_String -> {
                        val coverPoints = (0..1).map { getCoverPoint(initializer.valueArguments[it]) ?: return }
                        val binExpressions = initializer.valueArguments.drop(2)
                        val coverCross = ECoverCross(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            coverPoints,
                            ArrayList(binExpressions)
                        )
                        referenceUpdater.replace(property, coverCross)
                    }
                    Core.Vk.F_cc_CoverPoint_CoverPoint_CoverPoint_String -> {
                        val coverPoints = (0..2).map { getCoverPoint(initializer.valueArguments[it]) ?: return }
                        val binExpressions = initializer.valueArguments.drop(3)
                        val coverCross = ECoverCross(
                            property.location,
                            property.endLocation,
                            property.name,
                            property.annotationEntries,
                            property.documentationLines,
                            coverPoints,
                            ArrayList(binExpressions)
                        )
                        referenceUpdater.replace(property, coverCross)
                    }
                }
            }
        }

        private fun getCoverPoint(expression: EExpression): ECoverPoint? {
            if (expression is EReferenceExpression) {
                val reference = expression.reference
                if (reference is ECoverPoint) return reference
            }
            Messages.COVER_POINT_EXPECTED.on(expression)
            return null
        }
    }
}
