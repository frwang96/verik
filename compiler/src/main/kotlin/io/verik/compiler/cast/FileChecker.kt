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

import io.verik.compiler.ast.common.PackageName
import io.verik.compiler.ast.common.TreeVisitor
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import java.nio.file.Paths

object FileChecker {

    fun check(projectContext: ProjectContext) {
        projectContext.vkFiles.forEach {
            it.accept(FileVisitor)
        }
    }

    object FileVisitor: TreeVisitor() {

        override fun visitFile(file: VkFile) {
            val pathPackageName = (0 until (file.relativePath.nameCount - 1))
                .joinToString(separator = ".") { file.relativePath.getName(it).toString() }
                .let { PackageName(it) }

            if (file.packageName != pathPackageName)
                messageCollector.error("Package directive does not match file location", file)
            if (file.packageName == PackageName.ROOT)
                messageCollector.error("Use of the root package is prohibited", file)
            if (file.packageName == PackageName.CORE)
                messageCollector.error("Package name not permitted: ${file.packageName}", file)
            if (file.inputPath.fileName == Paths.get("Pkg.kt"))
                messageCollector.error("File name not permitted: ${file.inputPath.fileName}", file)
        }
    }
}