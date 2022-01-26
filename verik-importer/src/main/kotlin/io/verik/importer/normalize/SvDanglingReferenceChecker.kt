/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.normalize

import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.sv.element.common.SvElement
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.ast.sv.element.expression.SvReferenceExpression
import io.verik.importer.common.Declaration
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.common.Type
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object SvDanglingReferenceChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val danglingReferenceIndexerVisitor = DanglingReferenceIndexerVisitor()
        projectContext.compilationUnit.accept(danglingReferenceIndexerVisitor)
        val danglingReferenceCheckerVisitor = DanglingReferenceCheckerVisitor(
            danglingReferenceIndexerVisitor.declarations,
            projectStage
        )
        projectContext.compilationUnit.accept(danglingReferenceCheckerVisitor)
    }

    private class DanglingReferenceIndexerVisitor : SvTreeVisitor() {

        val declarations = HashSet<SvDeclaration>()

        override fun visitDeclaration(declaration: SvDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    private class DanglingReferenceCheckerVisitor(
        private val declarations: HashSet<SvDeclaration>,
        private val projectStage: ProjectStage
    ) : SvTreeVisitor() {

        private fun checkReference(type: Type, element: SvElement) {
            type.arguments.forEach { checkReference(it, element) }
            checkReference(type.reference, element)
        }

        private fun checkReference(reference: Declaration, element: SvElement) {
            if ((reference is SvDeclaration || reference is KtDeclaration) && reference !in declarations) {
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Dangling reference to ${reference.name} in $element"
                )
            }
        }

        override fun visitElement(element: SvElement) {
            super.visitElement(element)
            if (element is SvDescriptor) {
                checkReference(element.type, element)
            }
            if (element is SvReferenceExpression) {
                checkReference(element.reference, element)
            }
        }
    }
}
