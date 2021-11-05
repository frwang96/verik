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

import io.verik.compiler.ast.element.kt.EAnnotation
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.element.kt.EKtFunction
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.Annotations
import io.verik.compiler.core.common.Core
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object AnnotationCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(AnnotationCheckerVisitor)
    }

    private object AnnotationCheckerVisitor : TreeVisitor() {

        private val conflictingFunctionAnnotations = listOf(
            Annotations.COM,
            Annotations.SEQ,
            Annotations.RUN,
            Annotations.TASK
        )

        private fun checkFunctionAnnotations(annotations: List<EAnnotation>) {
            var conflictingAnnotation: String? = null
            for (annotation in annotations) {
                if (annotation.qualifiedName in conflictingFunctionAnnotations) {
                    if (conflictingAnnotation == null) {
                        conflictingAnnotation = annotation.name
                    } else {
                        Messages.CONFLICTING_ANNOTATION.on(annotation, conflictingAnnotation)
                    }
                }
            }
        }

        override fun visitKtBasicClass(basicClass: EKtBasicClass) {
            super.visitKtBasicClass(basicClass)
            if (basicClass.hasAnnotation(Annotations.SYNTHESIS_TOP) ||
                basicClass.hasAnnotation(Annotations.SIMULATION_TOP)
            ) {
                val basicClassType = basicClass.toType()
                if (!basicClassType.isSubtype(Core.Vk.C_Module.toType()))
                    Messages.TOP_NOT_MODULE.on(basicClass)
            }
        }

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            checkFunctionAnnotations(function.annotations)
        }
    }
}
