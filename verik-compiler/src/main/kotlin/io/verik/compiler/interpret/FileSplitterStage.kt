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

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.common.EDeclaration
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.EAbstractComponent
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object FileSplitterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val componentFiles = ArrayList<EFile>()
        projectContext.project.basicPackages.forEach { basicPackage ->
            val packageFiles = ArrayList<EFile>()
            basicPackage.files.forEach { file ->
                val baseFileName = file.inputPath.fileName.toString().removeSuffix(".kt")
                val packageDeclarations = ArrayList<EDeclaration>()
                val componentDeclarations = ArrayList<EDeclaration>()
                file.declarations.forEach {
                    if (isPackageDeclaration(it)) {
                        packageDeclarations.add(it)
                    } else {
                        componentDeclarations.add(it)
                    }
                }

                if (packageDeclarations.isNotEmpty()) {
                    val packageFilePath = basicPackage.getOutputPathNotNull().resolve("$baseFileName.svh")
                    val packageFile = EFile(
                        file.location,
                        file.inputPath,
                        packageFilePath,
                        packageDeclarations
                    )
                    packageFiles.add(packageFile)
                }

                if (componentDeclarations.isNotEmpty()) {
                    val componentFilePath = basicPackage.getOutputPathNotNull().resolve("$baseFileName.sv")
                    val componentFile = EFile(
                        file.location,
                        file.inputPath,
                        componentFilePath,
                        componentDeclarations
                    )
                    componentFiles.add(componentFile)
                }
            }
            packageFiles.forEach { it.parent = basicPackage }
            basicPackage.files = packageFiles
        }
        componentFiles.forEach { it.parent = projectContext.project.rootPackage }
        projectContext.project.rootPackage.files = componentFiles

        projectContext.project.externBasicPackages.forEach { basicPackage ->
            basicPackage.files.forEach { file ->
                file.declarations.forEach {
                    if (!isPackageDeclaration(it))
                        Messages.EXTERN_INVALID_PACKAGE_DECLARATION.on(it)
                }
            }
        }

        projectContext.project.externRootPackage.files.forEach { file ->
            file.declarations.forEach {
                if (isPackageDeclaration(it))
                    Messages.EXTERN_INVALID_COMPONENT_DECLARATION.on(it)
            }
        }
    }

    private fun isPackageDeclaration(declaration: EDeclaration): Boolean {
        return when (declaration) {
            is EAbstractComponent -> false
            is ESvBasicClass -> true
            is EEnum -> true
            is EStruct -> true
            is ESvFunction -> true
            is ETask -> true
            is ESvProperty -> true
            is ESvEnumEntry -> true
            else -> Messages.INTERNAL_ERROR.on(
                declaration,
                "Unable to identify as component or package declaration: ${declaration.name}"
            )
        }
    }
}
