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

package io.verik.compiler.check

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkImportDirective
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector

object ImportDirectiveChecker {

    fun check(projectContext: ProjectContext) {
        val mainPackageNameSet = HashSet<Name>()
        val testPackageNameSet = HashSet<Name>()
        projectContext.vkFiles.forEach {
            when (it.sourceSetType) {
                SourceSetType.MAIN -> mainPackageNameSet.add(it.packageName)
                SourceSetType.TEST -> testPackageNameSet.add(it.packageName)
            }
        }
        mainPackageNameSet.intersect(testPackageNameSet).forEach {
            messageCollector.error("Main and test packages must be distinct: $it", null)
        }

        val packageNameSet = mainPackageNameSet.union(testPackageNameSet)
        val importDirectiveVisitor = ImportDirectiveVisitor(packageNameSet)
        projectContext.vkFiles.forEach { it.accept(importDirectiveVisitor) }
    }

    class ImportDirectiveVisitor(val packageNameSet: Set<Name>): TreeVisitor() {

        override fun visitImportDirective(importDirective: VkImportDirective) {
            if (importDirective.packageName !in packageNameSet && importDirective.packageName.name != "io.verik.core")
                messageCollector.error("Import package not found: ${importDirective.packageName}", importDirective)
        }
    }
}