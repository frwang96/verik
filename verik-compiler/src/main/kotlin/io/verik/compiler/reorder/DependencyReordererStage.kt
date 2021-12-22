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

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EPackage
import io.verik.compiler.ast.element.common.EProject
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

        private fun reorderPackages(dependencies: List<Dependency>) {
            dependencies.forEach {
                val fromPackage = it.fromDeclaration.cast<EPackage>()
                val toPackage = it.toDeclaration.cast<EPackage>()
                if (fromPackage.packageType == PackageType.NATIVE_REGULAR &&
                    toPackage.packageType == PackageType.NATIVE_ROOT
                ) {
                    Messages.PACKAGE_DEPENDENCY_ILLEGAL.on(it.element, it)
                }
            }
        }

        private fun reorderDeclarations(file: EFile, dependencies: List<Dependency>) {
            val dependencyReordererResult = DependencyReorderer.reorder(file.declarations, dependencies)
            dependencyReordererResult.unsatisfiedDependencies.forEach {
                Messages.DECLARATION_CIRCULAR_DEPENDENCY.on(it.element, it)
            }
            file.declarations = ArrayList(dependencyReordererResult.reorderedDeclarations)
        }

        override fun visitProject(project: EProject) {
            super.visitProject(project)
            reorderPackages(dependencyRegistry.getDependencies(project))
        }

        override fun visitFile(file: EFile) {
            super.visitFile(file)
            reorderDeclarations(file, dependencyRegistry.getDependencies(file))
        }
    }
}
