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

package io.verik.compiler.check.mid

import io.verik.compiler.ast.common.TypeParameterized
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.kt.ECompanionObject
import io.verik.compiler.ast.element.declaration.kt.EKtFunction
import io.verik.compiler.ast.element.declaration.kt.EKtValueParameter
import io.verik.compiler.ast.property.AnnotationEntry
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.AnnotationEntries
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object AnnotationEntryCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(AnnotationEntryCheckerVisitor)
    }

    private object AnnotationEntryCheckerVisitor : TreeVisitor() {

        private val conflictingAnnotationEntries = listOf(
            AnnotationEntries.COM,
            AnnotationEntries.SEQ,
            AnnotationEntries.RUN,
            AnnotationEntries.TASK
        )

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            val isEntryPoint = declaration.hasAnnotationEntry(AnnotationEntries.ENTRY_POINT)
            if (isEntryPoint) {
                if (declaration is TypeParameterized && declaration.typeParameters.isNotEmpty()) {
                    Messages.ENTRY_POINT_PARAMETERIZED.on(declaration)
                }
                val parent = declaration.parent
                if (parent !is EFile && parent !is ECompanionObject) {
                    Messages.INVALID_ENTRY_POINT.on(declaration)
                }
            }
        }

        override fun visitKtFunction(function: EKtFunction) {
            super.visitKtFunction(function)
            var conflictingAnnotationEntry: AnnotationEntry? = null
            for (annotationEntry in function.annotationEntries) {
                if (annotationEntry in conflictingAnnotationEntries) {
                    if (conflictingAnnotationEntry == null) {
                        conflictingAnnotationEntry = annotationEntry
                    } else {
                        Messages.CONFLICTING_ANNOTATIONS.on(function, annotationEntry, conflictingAnnotationEntry)
                    }
                }
            }
        }

        override fun visitProperty(property: EProperty) {
            super.visitProperty(property)
            val isComAssignment = property.hasAnnotationEntry(AnnotationEntries.COM)
            val isSeqAssignment = property.hasAnnotationEntry(AnnotationEntries.SEQ)
            when {
                isComAssignment && isSeqAssignment ->
                    Messages.CONFLICTING_ANNOTATIONS.on(property, AnnotationEntries.COM, AnnotationEntries.SEQ)
                isComAssignment -> {
                    if (!property.isMutable) {
                        Messages.PROCEDURAL_ASSIGNMENT_NOT_MUTABLE.on(property, "Combinational", property.name)
                    }
                }
                isSeqAssignment -> {
                    if (!property.isMutable) {
                        Messages.PROCEDURAL_ASSIGNMENT_NOT_MUTABLE.on(property, "Sequential", property.name)
                    }
                }
            }
        }

        override fun visitKtValueParameter(valueParameter: EKtValueParameter) {
            super.visitKtValueParameter(valueParameter)
            if (valueParameter.hasAnnotationEntry(AnnotationEntries.IN) &&
                valueParameter.hasAnnotationEntry(AnnotationEntries.OUT)
            ) {
                Messages.CONFLICTING_ANNOTATIONS.on(valueParameter, AnnotationEntries.IN, AnnotationEntries.OUT)
            }
        }
    }
}
