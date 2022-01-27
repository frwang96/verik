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

import io.verik.importer.ast.common.Type
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.ast.element.declaration.EPackage
import io.verik.importer.ast.kt.element.KtClass
import io.verik.importer.ast.kt.element.KtDeclaration
import io.verik.importer.ast.kt.element.KtElement
import io.verik.importer.ast.kt.element.KtFile
import io.verik.importer.ast.kt.element.KtPackage
import io.verik.importer.ast.kt.element.KtProject
import io.verik.importer.common.KtTreeVisitor
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.SourceLocation
import java.nio.file.Path

object InterpreterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val interpreterMap = InterpreterMap()
        val declarationInterpreter = DeclarationInterpreter(interpreterMap)
        projectContext.project = interpretProject(projectContext, declarationInterpreter)
        val referenceForwarderVisitor = ReferenceForwarderVisitor(interpreterMap)
        projectContext.project.accept(referenceForwarderVisitor)
    }

    private fun interpretProject(
        projectContext: ProjectContext,
        declarationInterpreter: DeclarationInterpreter
    ): KtProject {
        val packages = ArrayList<KtPackage>()
        val rootPackage = interpretPackage(
            SourceLocation.NULL,
            projectContext.config.rootPackageName,
            projectContext.compilationUnit.declarations,
            projectContext.config.rootPackageOutputPath,
            declarationInterpreter
        )
        if (rootPackage != null)
            packages.add(rootPackage)

        projectContext.compilationUnit.declarations.forEach {
            if (it is EPackage) {
                val `package` = interpretPackage(
                    it.location,
                    "${projectContext.config.rootPackageName}.${it.name}",
                    it.declarations,
                    projectContext.config.rootPackageOutputPath.resolve(it.name),
                    declarationInterpreter
                )
                if (`package` != null)
                    packages.add(`package`)
            }
        }
        return KtProject(packages)
    }

    private fun interpretPackage(
        location: SourceLocation,
        name: String,
        declarations: List<EDeclaration>,
        outputPath: Path,
        declarationInterpreter: DeclarationInterpreter
    ): KtPackage? {
        val interpretedDeclarations = declarations.mapNotNull { declarationInterpreter.interpretDeclaration(it) }
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

    private class ReferenceForwarderVisitor(
        private val interpreterMap: InterpreterMap
    ) : KtTreeVisitor() {

        private fun forwardReferences(type: Type) {
            type.arguments.forEach { forwardReferences(it) }
            type.reference = interpreterMap.getDeclaration(type.reference)
        }

        override fun visitElement(element: KtElement) {
            super.visitElement(element)
            if (element is KtDeclaration) {
                forwardReferences(element.type)
            }
            if (element is KtClass) {
                forwardReferences(element.superType)
            }
        }
    }
}
