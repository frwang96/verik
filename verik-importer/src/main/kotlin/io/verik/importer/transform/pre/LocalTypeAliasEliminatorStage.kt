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

package io.verik.importer.transform.pre

import io.verik.importer.ast.element.declaration.EContainerDeclaration
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ETypeAlias
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.common.ElementCopier
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object LocalTypeAliasEliminatorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.accept(LocalTypeAliasSubstitutorVisitor)
        projectContext.project.accept(LocalTypeAliasEliminatorVisitor)
    }

    private object LocalTypeAliasSubstitutorVisitor : TreeVisitor() {

        override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            val reference = referenceDescriptor.type.reference
            if (reference is ETypeAlias) {
                val parent = reference.parent
                if (parent is ESvClass || parent is EModule) {
                    val descriptor = ElementCopier.deepCopy(reference.descriptor, referenceDescriptor.location)
                    referenceDescriptor.replace(descriptor)
                }
            }
        }
    }

    private object LocalTypeAliasEliminatorVisitor : TreeVisitor() {

        override fun visitContainerDeclaration(containerDeclaration: EContainerDeclaration) {
            super.visitContainerDeclaration(containerDeclaration)
            if (containerDeclaration is ESvClass || containerDeclaration is EModule) {
                val declarations = containerDeclaration.declarations.filter { it !is ETypeAlias }
                containerDeclaration.declarations = ArrayList(declarations)
            }
        }
    }
}
