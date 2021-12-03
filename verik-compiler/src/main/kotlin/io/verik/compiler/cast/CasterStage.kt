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
import java.nio.file.Path

object CasterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val filesMap = HashMap<String, ArrayList<EFile>>()

        projectContext.getKtFiles().forEach { file ->
            val location = file.location()
            val packageName = file.packageFqName.asString()
            val inputPath = Platform.getPathFromString(file.virtualFilePath)
            val baseFileName = inputPath.fileName.toString().substringBeforeLast(".")
            val fileName = if (packageName in listOf("", "imported")) {
                "$baseFileName.sv"
            } else {
                "$baseFileName.svh"
            }
            val outputPath = getPackageOutputPath(packageName, projectContext).resolve(fileName)
            val declarations = file.declarations.mapNotNull {
                projectContext.castContext.casterVisitor.getDeclaration(it)
            }

            val castedFile = EFile(location, inputPath, outputPath, ArrayList(declarations))
            val files = filesMap[packageName]
            if (files != null) {
                files.add(castedFile)
            } else {
                filesMap[packageName] = arrayListOf(castedFile)
            }
        }

        val basicPackages = ArrayList<EBasicPackage>()
        val rootPackage = ERootPackage(
            SourceLocation.NULL,
            ArrayList(),
            getPackageOutputPath("", projectContext)
        )
        val importedBasicPackages = ArrayList<EBasicPackage>()
        val importedRootPackage = ERootPackage(
            SourceLocation.NULL,
            ArrayList(),
            getPackageOutputPath("imported", projectContext)
        )

        filesMap.forEach { (packageName, files) ->
            when {
                packageName == "" -> {
                    files.forEach { it.parent = rootPackage }
                    rootPackage.files = files
                }
                packageName == "imported" -> {
                    files.forEach { it.parent = importedRootPackage }
                    importedRootPackage.files = files
                }
                packageName.startsWith("imported.") -> {
                    val basicPackage = EBasicPackage(
                        SourceLocation.NULL,
                        packageName,
                        files,
                        getPackageOutputPath(packageName, projectContext)
                    )
                    importedBasicPackages.add(basicPackage)
                }
                else -> {
                    val basicPackage = EBasicPackage(
                        SourceLocation.NULL,
                        packageName,
                        files,
                        getPackageOutputPath(packageName, projectContext)
                    )
                    basicPackages.add(basicPackage)
                }
            }
        }

        val project = EProject(
            SourceLocation.NULL,
            basicPackages,
            importedBasicPackages,
            rootPackage,
            importedRootPackage
        )
        projectContext.project = project
    }

    private fun getPackageOutputPath(packageName: String, projectContext: ProjectContext): Path {
        val relativePath = packageName.replace(".", Platform.separator)
        return projectContext.config.outputSourceDir.resolve(relativePath)
    }
}
