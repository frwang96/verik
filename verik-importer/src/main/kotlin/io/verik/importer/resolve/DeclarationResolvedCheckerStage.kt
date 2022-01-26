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

package io.verik.importer.resolve

import io.verik.importer.ast.sv.element.declaration.SvContainerDeclaration
import io.verik.importer.ast.sv.element.declaration.SvDeclaration
import io.verik.importer.ast.sv.element.descriptor.SvDescriptor
import io.verik.importer.common.SvTreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object DeclarationResolvedCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.compilationUnit.accept(DeclarationResolvedIndexerVisitor)
    }

    private object DeclarationResolvedIndexerVisitor : SvTreeVisitor() {

        override fun visitContainerDeclaration(containerDeclaration: SvContainerDeclaration) {
            super.visitContainerDeclaration(containerDeclaration)
            val declarations = ArrayList<SvDeclaration>()
            containerDeclaration.declarations.forEach {
                val declarationResolvedCheckerVisitor = DeclarationResolvedCheckerVisitor()
                it.accept(declarationResolvedCheckerVisitor)
                if (declarationResolvedCheckerVisitor.isResolved) {
                    declarations.add(it)
                }
            }
            containerDeclaration.declarations = declarations
        }
    }

    private class DeclarationResolvedCheckerVisitor : SvTreeVisitor() {

        var isResolved = true

        override fun visitDescriptor(descriptor: SvDescriptor) {
            if (!isResolved)
                return
            super.visitDescriptor(descriptor)
            if (!descriptor.type.isResolved()) {
                isResolved = false
            }
        }
    }
}
