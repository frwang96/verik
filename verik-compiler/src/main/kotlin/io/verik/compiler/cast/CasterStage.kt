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

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.common.location
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.SourceLocation

object CasterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val files = HashMap<String, ArrayList<EFile>>()

        projectContext.getKtFiles().forEach { file ->
            val location = file.location()
            val packageName = file.packageFqName.asString()
            val inputPath = Platform.getPathFromString(file.virtualFilePath)
            val declarations = file.declarations.mapNotNull {
                projectContext.castContext.casterVisitor.getDeclaration(it)
            }
            if (packageName !in files)
                files[packageName] = ArrayList()
            files[packageName]!!.add(EFile(location, inputPath, null, ArrayList(declarations)))
        }

        val basicPackages = ArrayList<EBasicPackage>()
        val importedBasicPackages = ArrayList<EBasicPackage>()
        var importedRootPackage: ERootPackage? = null
        files.forEach { (packageName, files) ->
            when {
                packageName == "imported" ->
                    importedRootPackage = ERootPackage(SourceLocation.NULL, files)
                packageName.startsWith("imported.") ->
                    importedBasicPackages.add(EBasicPackage(SourceLocation.NULL, packageName, files, null))
                else -> {
                    val relativePath = packageName.replace(".", Platform.separator)
                    val outputPath = projectContext.config.outputSourceDir.resolve(relativePath)
                    basicPackages.add(EBasicPackage(SourceLocation.NULL, packageName, files, outputPath))
                }
            }
        }

        val project = EProject(
            SourceLocation.NULL,
            basicPackages,
            importedBasicPackages,
            ERootPackage(SourceLocation.NULL, ArrayList()),
            importedRootPackage ?: ERootPackage(SourceLocation.NULL, ArrayList())
        )
        projectContext.project = project
    }
}
