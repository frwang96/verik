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

package io.verik.compiler.reorder

import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EAbstractContainerClass
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.element.declaration.sv.EAbstractContainerComponent
import io.verik.compiler.ast.property.PackageType
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
                if (fromPackage.packageType == PackageType.REGULAR_NON_ROOT && toPackage.packageType.isRoot()) {
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

        private fun reorderFiles(`package`: EPackage, dependencies: List<Dependency>) {
            val dependencyReordererResult = DependencyReorderer.reorder(`package`.files, dependencies)
            dependencyReordererResult.unsatisfiedDependencies.forEach {
                Messages.CIRCULAR_FILE_DEPENDENCY.on(it.element, it)
            }
            `package`.files = ArrayList(dependencyReordererResult.reorderedDeclarations)
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

        override fun visitPackage(`package`: EPackage) {
            if (!`package`.packageType.isImported()) {
                super.visitPackage(`package`)
                reorderFiles(`package`, dependencyRegistry.getDependencies(`package`))
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
