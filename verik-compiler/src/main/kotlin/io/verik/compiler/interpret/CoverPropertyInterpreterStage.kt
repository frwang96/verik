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
import io.verik.compiler.ast.element.declaration.sv.ECoverPoint
import io.verik.compiler.ast.element.expression.common.ECallExpression
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.common.ReferenceUpdater
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage

object CoverPropertyInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val referenceUpdater = ReferenceUpdater(projectContext)
        val coverPropertyInterpreterVisitor = CoverPropertyInterpreterVisitor(referenceUpdater)
        projectContext.project.accept(coverPropertyInterpreterVisitor)
        referenceUpdater.flush()
    }

    private class CoverPropertyInterpreterVisitor(
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
}
