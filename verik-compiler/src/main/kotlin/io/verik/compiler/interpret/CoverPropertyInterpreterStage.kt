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

        private fun interpretCoverPoint(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cp_Any) {
                val coverPoint = ECoverPoint(
                    property.location,
                    property.endLocation,
                    property.name,
                    property.annotationEntries,
                    property.documentationLines,
                    initializer.valueArguments[0]
                )
                referenceUpdater.replace(property, coverPoint)
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.Cover) && initializer != null) {
                if (property.type.reference == Core.Vk.C_CoverPoint) {
                    interpretCoverPoint(property, initializer)
                }
            }
        }
    }

    private class CoverCrossInterpreterVisitor(
        private val referenceUpdater: ReferenceUpdater
    ) : TreeVisitor() {

        private fun interpretCoverCross(property: EProperty, initializer: EExpression) {
            if (initializer is ECallExpression && initializer.reference == Core.Vk.F_cc_Any) {
                if (initializer.valueArguments.size < 2) {
                    Messages.COVER_CROSS_INSUFFICIENT_ARGUMENTS.on(initializer)
                }
                val coverPoints = initializer.valueArguments.mapNotNull {
                    if (it is EReferenceExpression) {
                        val reference = it.reference
                        if (reference is ECoverPoint) {
                            return@mapNotNull reference
                        }
                    }
                    Messages.COVER_POINT_EXPECTED.on(it)
                    null
                }
                val coverCross = ECoverCross(
                    property.location,
                    property.endLocation,
                    property.name,
                    property.annotationEntries,
                    property.documentationLines,
                    coverPoints
                )
                referenceUpdater.replace(property, coverCross)
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val initializer = property.initializer
            if (property.hasAnnotationEntry(AnnotationEntries.Cover) && initializer != null) {
                if (property.type.reference == Core.Vk.C_CoverCross) {
                    interpretCoverCross(property, initializer)
                }
            }
        }
    }
}
