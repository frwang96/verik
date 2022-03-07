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

import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EPackage
import io.verik.compiler.ast.property.PackageKind
import io.verik.compiler.common.location
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.SourceLocation
import java.nio.file.Path

object CasterStage : ProjectStage() {

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
                projectContext.castContext.castDeclaration(it)
            }

            val castedFile = EFile(location, inputPath, outputPath, ArrayList(declarations))
            val files = filesMap[packageName]
            if (files != null) {
                files.add(castedFile)
            } else {
                filesMap[packageName] = arrayListOf(castedFile)
            }
        }

        val regularNonRootPackages = ArrayList<EPackage>()
        val regularRootPackage = EPackage(
            SourceLocation.NULL,
            "<root>",
            ArrayList(),
            ArrayList(),
            PackageKind.REGULAR_ROOT
        )
        val importedNonRootPackages = ArrayList<EPackage>()
        val importedRootPackage = EPackage(
            SourceLocation.NULL,
            "imported",
            ArrayList(),
            ArrayList(),
            PackageKind.IMPORTED_ROOT
        )

        filesMap.forEach { (packageName, files) ->
            when {
                packageName == "" -> {
                    files.forEach { it.parent = regularRootPackage }
                    regularRootPackage.files = files
                }
                packageName == "imported" -> {
                    files.forEach { it.parent = importedRootPackage }
                    importedRootPackage.files = files
                }
                packageName.startsWith("imported.") -> {
                    importedNonRootPackages.add(
                        EPackage(
                            SourceLocation.NULL,
                            packageName,
                            files,
                            ArrayList(),
                            PackageKind.IMPORTED_NON_ROOT
                        )
                    )
                }
                else -> {
                    regularNonRootPackages.add(
                        EPackage(
                            SourceLocation.NULL,
                            packageName,
                            files,
                            ArrayList(),
                            PackageKind.REGULAR_NON_ROOT
                        )
                    )
                }
            }
        }

        val project = EProject(
            SourceLocation.NULL,
            regularNonRootPackages,
            regularRootPackage,
            importedNonRootPackages,
            importedRootPackage
        )
        projectContext.project = project
    }

    private fun getPackageOutputPath(packageName: String, projectContext: ProjectContext): Path {
        val relativePath = packageName.replace(".", Platform.separator)
        return projectContext.config.outputSourceDir.resolve(relativePath)
    }
}
