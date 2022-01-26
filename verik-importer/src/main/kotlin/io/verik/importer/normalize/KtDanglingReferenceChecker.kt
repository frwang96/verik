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

import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.common.Declaration
import io.verik.importer.common.KtTreeVisitor
import io.verik.importer.common.Type
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object KtDanglingReferenceChecker : NormalizationStage {

    override fun process(projectContext: ProjectContext, projectStage: ProjectStage) {
        val danglingReferenceIndexerVisitor = DanglingReferenceIndexerVisitor()
        projectContext.project.accept(danglingReferenceIndexerVisitor)
        val danglingReferenceCheckerVisitor = DanglingReferenceCheckerVisitor(
            danglingReferenceIndexerVisitor.declarations,
            projectStage
        )
        projectContext.project.accept(danglingReferenceCheckerVisitor)
    }

    private class DanglingReferenceIndexerVisitor : KtTreeVisitor() {

        val declarations = HashSet<KtDeclaration>()

        override fun visitDeclaration(declaration: KtDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    private class DanglingReferenceCheckerVisitor(
        private val declarations: HashSet<KtDeclaration>,
        private val projectStage: ProjectStage
    ) : KtTreeVisitor() {

        private fun checkReference(type: Type, element: KtElement) {
            type.arguments.forEach { checkReference(it, element) }
            checkReference(type.reference, element)
        }

        private fun checkReference(reference: Declaration, element: KtElement) {
            if ((reference is SvDeclaration || reference is KtDeclaration) && reference !in declarations) {
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Dangling reference to ${reference.name} in $element"
                )
            }
        }

        override fun visitElement(element: KtElement) {
            super.visitElement(element)
            if (element is KtDeclaration) {
                checkReference(element.type, element)
            }
            if (element is KtClass) {
                checkReference(element.superType, element)
            }
        }
    }
}
