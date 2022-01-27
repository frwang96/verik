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

import io.verik.importer.ast.common.Declaration
import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.ast.element.expression.EReferenceExpression
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object DanglingReferenceChecker : NormalizationChecker {

    override fun check(projectContext: ProjectContext, projectStage: ProjectStage) {
        val danglingReferenceIndexerVisitor = DanglingReferenceIndexerVisitor()
        projectContext.compilationUnit.accept(danglingReferenceIndexerVisitor)
        val danglingReferenceCheckerVisitor = DanglingReferenceCheckerVisitor(
            danglingReferenceIndexerVisitor.declarations,
            projectStage
        )
        projectContext.compilationUnit.accept(danglingReferenceCheckerVisitor)
    }

    private class DanglingReferenceIndexerVisitor : TreeVisitor() {

        val declarations = HashSet<EDeclaration>()

        override fun visitDeclaration(declaration: EDeclaration) {
            super.visitDeclaration(declaration)
            declarations.add(declaration)
        }
    }

    private class DanglingReferenceCheckerVisitor(
        private val declarations: HashSet<EDeclaration>,
        private val projectStage: ProjectStage
    ) : TreeVisitor() {

        private fun checkReference(type: Type, element: EElement) {
            type.arguments.forEach { checkReference(it, element) }
            checkReference(type.reference, element)
        }

        private fun checkReference(reference: Declaration, element: EElement) {
            if (reference is EDeclaration && reference !in declarations) {
                Messages.NORMALIZATION_ERROR.on(
                    element,
                    projectStage,
                    "Dangling reference to ${reference.name} in $element"
                )
            }
        }

        override fun visitElement(element: EElement) {
            super.visitElement(element)
            if (element is EDescriptor) {
                checkReference(element.type, element)
            }
            if (element is EReferenceExpression) {
                checkReference(element.reference, element)
            }
        }
    }
}
