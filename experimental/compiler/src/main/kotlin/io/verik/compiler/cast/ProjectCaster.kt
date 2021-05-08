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

import io.verik.compiler.ast.Name
import io.verik.compiler.ast.SourceSetType
import io.verik.compiler.ast.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import io.verik.compiler.util.ElementUtil

object ProjectCaster {

    fun cast(projectContext: ProjectContext) {
        val casterVisitor = CasterVisitor(projectContext)
        val files = projectContext.ktFiles.mapNotNull {
            ElementUtil.cast<VkFile>(it.accept(casterVisitor, Unit))
        }
        checkPackageNames(files)
        projectContext.vkFiles = files
        messageCollector.flush()
    }

    private fun checkPackageNames(files: List<VkFile>) {
        val mainPackageNameSet = HashSet<Name>()
        val testPackageNameSet = HashSet<Name>()
        files.forEach {
            when (it.sourceSetType) {
                SourceSetType.MAIN -> mainPackageNameSet.add(it.packageName)
                SourceSetType.TEST -> testPackageNameSet.add(it.packageName)
            }
        }
        mainPackageNameSet.intersect(testPackageNameSet).forEach {
            messageCollector.error("Main and test packages must be distinct: $it")
        }
    }
}