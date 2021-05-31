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

package io.verik.compiler.cast

import io.verik.compiler.ast.common.SourceSetType
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.descriptor.PackageDescriptor
import io.verik.compiler.ast.element.VkImportDirective
import io.verik.compiler.core.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector

object ImportDirectiveChecker {

    fun check(projectContext: ProjectContext) {
        val mainPackageDescriptorSet = HashSet<PackageDescriptor>()
        val testPackageDescriptorSet = HashSet<PackageDescriptor>()
        projectContext.vkFiles.forEach {
            when (it.sourceSetType) {
                SourceSetType.MAIN -> mainPackageDescriptorSet.add(it.packageDescriptor)
                SourceSetType.TEST -> testPackageDescriptorSet.add(it.packageDescriptor)
            }
        }
        mainPackageDescriptorSet.intersect(testPackageDescriptorSet).forEach {
            messageCollector.error("Main and test packages must be distinct: $it", null)
        }

        val packageDescriptorSet = mainPackageDescriptorSet.union(testPackageDescriptorSet)
        val importDirectiveVisitor = ImportDirectiveVisitor(packageDescriptorSet)
        projectContext.vkFiles.forEach { it.accept(importDirectiveVisitor) }
    }

    class ImportDirectiveVisitor(private val packageDescriptorSet: Set<PackageDescriptor>): TreeVisitor() {

        override fun visitImportDirective(importDirective: VkImportDirective) {
            if (importDirective.packageDescriptor !in packageDescriptorSet
                && importDirective.packageDescriptor != CorePackage.CORE
            ) {
                messageCollector.error(
                    "Import package not found: ${importDirective.packageDescriptor}",
                    importDirective
                )
            }
        }
    }
}