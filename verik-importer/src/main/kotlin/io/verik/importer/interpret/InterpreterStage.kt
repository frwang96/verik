/*
 * Copyright (c) 2022 Francis Wang
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

package io.verik.importer.interpret

import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtFile
import io.verik.importer.ast.kt.element.KtPackage
import io.verik.importer.ast.kt.element.KtProject
import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.ast.sv.element.SvPackage
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

object InterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val compilationUnit = projectContext.compilationUnit
        val packages = ArrayList<KtPackage>()
        val rootPackage = interpretPackage(
            SourceLocation.NULL,
            projectContext.config.rootPackageName,
            compilationUnit.declarations,
            projectContext.config.rootPackageOutputPath
        )
        if (rootPackage != null)
            packages.add(rootPackage)

        compilationUnit.declarations.forEach {
            if (it is SvPackage) {
                val `package` = interpretPackage(
                    it.location,
                    "${projectContext.config.rootPackageName}.${it.name}",
                    it.declarations,
                    projectContext.config.rootPackageOutputPath.resolve(it.name)
                )
                if (`package` != null)
                    packages.add(`package`)
            }
        }

        val project = KtProject(packages)
        projectContext.project = project
    }

    private fun interpretPackage(
        location: SourceLocation,
        name: String,
        declarations: List<SvDeclaration>,
        outputPath: Path
    ): KtPackage? {
        val interpretedDeclarations = declarations.mapNotNull { DeclarationInterpreter.interpretDeclaration(it) }
        val fileDeclarationsMap = HashMap<String, ArrayList<KtDeclaration>>()
        interpretedDeclarations.forEach {
            val baseFileName = it.location.path.fileName.toString().substringBefore(".")
            val fileDeclarations = fileDeclarationsMap[baseFileName]
            if (fileDeclarations != null) {
                fileDeclarations.add(it)
            } else {
                fileDeclarationsMap[baseFileName] = arrayListOf(it)
            }
        }

        val files = ArrayList<KtFile>()
        fileDeclarationsMap.forEach { (baseFileName, fileDeclarations) ->
            val fileLocation = fileDeclarations.first().location
            val fileOutputPath = outputPath.resolve("$baseFileName.kt")
            val file = KtFile(fileLocation, fileOutputPath, fileDeclarations)
            files.add(file)
        }

        return if (files.isNotEmpty()) {
            KtPackage(location, name, files)
        } else null
    }
}
