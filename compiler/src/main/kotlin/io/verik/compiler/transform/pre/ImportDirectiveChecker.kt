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

package io.verik.compiler.transform.pre

import io.verik.compiler.ast.common.PackageDeclaration
import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkImportDirective
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.core.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ImportDirectiveChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val mainPackageDeclarationSet = HashSet<PackageDeclaration>()
        val testPackageDeclarationSet = HashSet<PackageDeclaration>()
        projectContext.vkFiles.forEach {
            when (it.sourceSetType) {
                SourceSetType.MAIN -> mainPackageDeclarationSet.add(it.packageDeclaration)
                SourceSetType.TEST -> testPackageDeclarationSet.add(it.packageDeclaration)
            }
        }
        mainPackageDeclarationSet.intersect(testPackageDeclarationSet).forEach {
            m.error("Main and test packages must be distinct: $it", null)
        }

        val packageDeclarationSet = HashSet<PackageDeclaration>()
        packageDeclarationSet.add(CorePackage.VERIK_CORE)
        packageDeclarationSet.addAll(mainPackageDeclarationSet)
        packageDeclarationSet.addAll(testPackageDeclarationSet)

        val importDirectiveVisitor = ImportDirectiveVisitor(packageDeclarationSet)
        projectContext.vkFiles.forEach { it.accept(importDirectiveVisitor) }
    }

    class ImportDirectiveVisitor(private val packageDeclarationSet: Set<PackageDeclaration>) : TreeVisitor() {

        override fun visitImportDirective(importDirective: VkImportDirective) {
            if (importDirective.packageDeclaration !in packageDeclarationSet)
                m.error("Import package not found: ${importDirective.packageDeclaration}", importDirective)
        }
    }
}