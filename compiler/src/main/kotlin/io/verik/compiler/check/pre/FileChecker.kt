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

package io.verik.compiler.check.pre

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.property.Name
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.common.TreeVisitor
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import java.nio.file.Paths

object FileChecker : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        projectContext.files.forEach {
            it.accept(FileVisitor)
        }
    }

    object FileVisitor : TreeVisitor() {

        override fun visitFile(file: EFile) {
            val pathPackageDeclaration = (0 until (file.relativePath.nameCount - 1))
                .joinToString(separator = ".") { file.relativePath.getName(it).toString() }
                .let { PackageDeclaration(Name(it)) }

            if (file.packageDeclaration != pathPackageDeclaration)
                m.error("Package directive does not match file location", file)
            if (file.packageDeclaration == CorePackage.ROOT)
                m.error("Use of the root package is prohibited", file)
            if (file.packageDeclaration in listOf(CorePackage.VK, CorePackage.SV))
                m.error("Package name is reserved: ${file.packageDeclaration.name}", file)
            if (file.inputPath.fileName == Paths.get("Pkg.kt"))
                m.error("File name is reserved: ${file.inputPath.fileName}", file)
        }
    }
}