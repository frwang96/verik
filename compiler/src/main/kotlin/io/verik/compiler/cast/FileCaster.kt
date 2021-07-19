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

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.common.PackageDeclaration
import io.verik.compiler.common.location
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import org.jetbrains.kotlin.psi.KtFile
import java.nio.file.Paths

object FileCaster {

    fun cast(projectContext: ProjectContext, castContext: CastContext, file: KtFile): FileCasterResult? {
        val location = file.location()
        val inputPath = Paths.get(file.virtualFilePath)
        val relativePath = when {
            inputPath.startsWith(projectContext.config.mainDir) ->
                projectContext.config.mainDir.relativize(inputPath)
            else -> {
                m.error("File should be located under main source directory", file)
                return null
            }
        }
        val packageName = file.packageFqName.asString()
        val packageDeclaration = PackageDeclaration(packageName)
        val baseCasterVisitor = BaseCasterVisitor(castContext)
        val members = file.declarations.map { it.accept(baseCasterVisitor, Unit) }

        return FileCasterResult(
            packageName,
            EFile(location, inputPath, null, relativePath, null, packageDeclaration, ArrayList(members))
        )
    }

    data class FileCasterResult(val packageName: String, val file: EFile)
}