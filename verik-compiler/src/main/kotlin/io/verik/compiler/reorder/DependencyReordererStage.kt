/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.property.PackageKind
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object DependencyReordererStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val dependencyRegistry = DependencyRegistry()
        val dependencyIndexerVisitor = DependencyIndexerVisitor(dependencyRegistry)
        projectContext.project.accept(dependencyIndexerVisitor)
        val dependencyReordererVisitor = DependencyReordererVisitor(dependencyRegistry)
        projectContext.project.accept(dependencyReordererVisitor)
    }

    private class DependencyReordererVisitor(
        private val dependencyRegistry: DependencyRegistry
    ) : TreeVisitor() {

        private fun reorderPackages(project: EProject, dependencies: List<Dependency>) {
            dependencies.forEach {
                val fromPackage = it.fromDeclaration.cast<EPackage>()
                val toPackage = it.toDeclaration.cast<EPackage>()
                if (fromPackage.kind == PackageKind.REGULAR_NON_ROOT && toPackage.kind.isRoot()) {
                    Messages.ILLEGAL_PACKAGE_DEPENDENCY.on(it.element, it)
                }
            }
            val dependencyReordererResult = DependencyReorderer.reorder(
                project.regularNonRootPackages,
                dependencies
            )
            dependencyReordererResult.unsatisfiedDependencies.forEach {
                Messages.CIRCULAR_PACKAGE_DEPENDENCY.on(it.element, it)
            }
            project.regularNonRootPackages = ArrayList(dependencyReordererResult.reorderedDeclarations)
        }

        private fun reorderFiles(pkg: EPackage, dependencies: List<Dependency>) {
            val dependencyReordererResult = DependencyReorderer.reorder(pkg.files, dependencies)
            dependencyReordererResult.unsatisfiedDependencies.forEach {
                Messages.CIRCULAR_FILE_DEPENDENCY.on(it.element, it)
            }
            pkg.files = ArrayList(dependencyReordererResult.reorderedDeclarations)
        }

        private fun reorderDeclarations(
            declarations: List<EDeclaration>,
            dependencies: List<Dependency>
        ): ArrayList<EDeclaration> {
            val dependencyReordererResult = DependencyReorderer.reorder(declarations, dependencies)
            dependencyReordererResult.unsatisfiedDependencies.forEach {
                Messages.CIRCULAR_DECLARATION_DEPENDENCY.on(it.element, it)
            }
            return ArrayList(dependencyReordererResult.reorderedDeclarations)
        }

        override fun visitProject(project: EProject) {
            super.visitProject(project)
            reorderPackages(project, dependencyRegistry.getDependencies(project))
        }

        override fun visitPackage(pkg: EPackage) {
            if (!pkg.kind.isImported()) {
                super.visitPackage(pkg)
                reorderFiles(pkg, dependencyRegistry.getDependencies(pkg))
            }
        }

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            file.declarations = reorderDeclarations(file.declarations, dependencyRegistry.getDependencies(file))
        }

        override fun visitAbstractContainerClass(abstractContainerClass: EAbstractContainerClass) {
            super.visitAbstractContainerClass(abstractContainerClass)
            abstractContainerClass.declarations = reorderDeclarations(
                abstractContainerClass.declarations,
                dependencyRegistry.getDependencies(abstractContainerClass)
            )
        }

        override fun visitAbstractContainerComponent(abstractContainerComponent: EAbstractContainerComponent) {
            super.visitAbstractContainerComponent(abstractContainerComponent)
            abstractContainerComponent.declarations = reorderDeclarations(
                abstractContainerComponent.declarations,
                dependencyRegistry.getDependencies(abstractContainerComponent)
            )
        }
    }
}
