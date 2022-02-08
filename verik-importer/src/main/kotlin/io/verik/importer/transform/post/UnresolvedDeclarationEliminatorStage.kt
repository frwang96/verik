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

package io.verik.importer.transform.post

import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.descriptor.EDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object UnresolvedDeclarationEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val unresolvedDeclarations = HashSet<EDeclaration>()
        val declarationResolvedIndexerVisitor = DeclarationResolvedIndexerVisitor(unresolvedDeclarations)
        var unresolvedDeclarationsCount: Int
        do {
            unresolvedDeclarationsCount = unresolvedDeclarations.size
            projectContext.project.accept(declarationResolvedIndexerVisitor)
        } while (unresolvedDeclarationsCount != unresolvedDeclarations.size)
    }

    private class DeclarationResolvedIndexerVisitor(
        private val unresolvedDeclarations: HashSet<EDeclaration>
    ) : TreeVisitor() {

        override fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
            super.visitContainerDeclaration(containerDeclaration)
            val declarations = ArrayList<EDeclaration>()
            containerDeclaration.declarations.forEach {
                val declarationResolvedCheckerVisitor = DeclarationResolvedCheckerVisitor(unresolvedDeclarations)
                it.accept(declarationResolvedCheckerVisitor)
                if (declarationResolvedCheckerVisitor.isResolved) {
                    declarations.add(it)
                } else {
                    unresolvedDeclarations.add(it)
                }
            }
            containerDeclaration.declarations = declarations
        }
    }

    private class DeclarationResolvedCheckerVisitor(
        private val unresolvedDeclarations: HashSet<EDeclaration>
    ) : TreeVisitor() {

        var isResolved = true

        override fun visitDescriptor(descriptor: EDescriptor) {
            if (!isResolved) return
            super.visitDescriptor(descriptor)
            if (!descriptor.type.isResolved() || descriptor.type.reference in unresolvedDeclarations) {
                isResolved = false
            }
        }
    }
}
