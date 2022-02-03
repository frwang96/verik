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

import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EModule
import io.verik.importer.ast.element.declaration.ESvClass
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.ast.element.descriptor.EReferenceDescriptor
import io.verik.importer.common.TreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages

object ReferenceResolverStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val namespaceIndexerVisitor = NamespaceIndexerVisitor()
        projectContext.project.accept(namespaceIndexerVisitor)
        val referenceResolverVisitor = ReferenceResolverVisitor(namespaceIndexerVisitor.namespaceMap)
        projectContext.project.accept(referenceResolverVisitor)
    }

    private class NamespaceIndexerVisitor : TreeVisitor() {

        val namespaceMap = NamespaceMap()

        private fun buildNamespace(declarations: List<EDeclaration>): Namespace {
            val namespace = Namespace()
            declarations.forEach {
                if (it !is ESvPackage) {
                    namespace[it.name] = it
                }
            }
            return namespace
        }

        override fun visitProject(project: EProject) {
            super.visitProject(project)
            namespaceMap[project] = buildNamespace(project.declarations)
        }

        override fun visitSvPackage(`package`: ESvPackage) {
            super.visitSvPackage(`package`)
            namespaceMap[`package`] = buildNamespace(`package`.declarations)
        }

        override fun visitSvClass(`class`: ESvClass) {
            super.visitSvClass(`class`)
            namespaceMap[`class`] = buildNamespace(`class`.declarations + `class`.typeParameters)
        }

        override fun visitModule(module: EModule) {
            super.visitModule(module)
            namespaceMap[module] = buildNamespace(module.declarations)
        }
    }

    private class ReferenceResolverVisitor(
        private val namespaceMap: NamespaceMap
    ) : TreeVisitor() {

        override fun visitReferenceDescriptor(referenceDescriptor: EReferenceDescriptor) {
            super.visitReferenceDescriptor(referenceDescriptor)
            val declaration = namespaceMap[referenceDescriptor, referenceDescriptor.name]
            if (declaration != null) {
                referenceDescriptor.type = declaration.toType()
            } else {
                Messages.UNRESOLVED_REFERENCE.on(referenceDescriptor, referenceDescriptor.name)
            }
        }
    }
}
