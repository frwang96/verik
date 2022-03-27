/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.transform.pre

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

        override fun visitSvPackage(pkg: ESvPackage) {
            super.visitSvPackage(pkg)
            namespaceMap[pkg] = buildNamespace(pkg.declarations)
        }

        override fun visitSvClass(cls: ESvClass) {
            super.visitSvClass(cls)
            namespaceMap[cls] = buildNamespace(cls.declarations + cls.typeParameters)
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
                referenceDescriptor.reference = declaration
            } else {
                Messages.UNRESOLVED_REFERENCE.on(referenceDescriptor, referenceDescriptor.name)
            }
        }
    }
}
