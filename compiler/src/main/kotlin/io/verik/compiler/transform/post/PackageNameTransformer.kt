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

package io.verik.compiler.transform.post

import io.verik.compiler.ast.common.Name
import io.verik.compiler.ast.common.PackageDeclaration
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.core.CorePackage
import io.verik.compiler.main.ProjectContext

object PackageNameTransformer {

    fun transform(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(PackageNameVisitor)
        }
    }

    object PackageNameVisitor : TreeVisitor() {

        private fun transformPackageName(packageDeclaration: PackageDeclaration): PackageDeclaration {
            val name = packageDeclaration.name.name
            return if (name == "") {
                CorePackage.ROOT
            } else {
                val names = name.split(".")
                PackageDeclaration(Name(names.joinToString(separator = "_", postfix = "_pkg")))
            }
        }

        override fun visitFile(file: VkFile) {
            file.packageDeclaration = transformPackageName(file.packageDeclaration)
        }
    }
}