/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.interpret

import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EKtFile
import io.verik.importer.ast.element.declaration.EKtPackage
import io.verik.importer.ast.element.declaration.ESvPackage
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

/**
 * Stage that interprets SystemVerilog packages to Kotlin packages.
 */
object PackageInterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val packages = ArrayList<EDeclaration>()
        val rootPackage = interpretPackage(
            SourceLocation.NULL,
            projectContext.config.rootPackageName,
            projectContext.project.declarations,
            projectContext.config.rootPackageOutputPath
        )
        if (rootPackage != null) {
            packages.add(rootPackage)
        }

        projectContext.project.declarations.forEach {
            if (it is ESvPackage) {
                val pkg = interpretPackage(
                    it.location,
                    "${projectContext.config.rootPackageName}.${it.name}",
                    it.declarations,
                    projectContext.config.rootPackageOutputPath.resolve(it.name)
                )
                if (pkg != null) packages.add(pkg)
            }
        }

        projectContext.project = EProject(packages)
    }

    private fun interpretPackage(
        location: SourceLocation,
        name: String,
        declarations: List<EDeclaration>,
        outputPath: Path
    ): EKtPackage? {
        val fileDeclarationsMap = HashMap<String, ArrayList<EDeclaration>>()
        declarations
            .filter { it !is ESvPackage }
            .forEach {
                val baseFileName = it.location.path.fileName.toString().substringBefore(".")
                val fileDeclarations = fileDeclarationsMap[baseFileName]
                if (fileDeclarations != null) {
                    fileDeclarations.add(it)
                } else {
                    fileDeclarationsMap[baseFileName] = arrayListOf(it)
                }
            }

        val files = ArrayList<EKtFile>()
        fileDeclarationsMap.forEach { (baseFileName, fileDeclarations) ->
            val fileLocation = fileDeclarations.first().location
            val fileOutputPath = outputPath.resolve("$baseFileName.kt")
            val file = EKtFile(fileLocation, fileDeclarations, fileOutputPath)
            files.add(file)
        }

        return if (files.isNotEmpty()) {
            EKtPackage(location, name, files)
        } else null
    }
}
