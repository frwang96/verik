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
        val rootPackage = projectContext.project.rootPackage
        projectContext.project.basicPackages.forEach { basicPackage ->
            val basicPackageFiles = ArrayList<EFile>()
            basicPackage.files.forEach {
                val splitResult = splitFile(it)
                if (splitResult.basicPackageFile != null) {
                    basicPackageFiles.add(splitResult.basicPackageFile)
                }
                if (splitResult.rootPackageFile != null) {
                    splitResult.rootPackageFile.parent = rootPackage
                    rootPackage.files.add(splitResult.rootPackageFile)
                }
            }
            basicPackage.files = basicPackageFiles
        }
    }

    private fun splitFile(file: EFile): SplitResult {
        val basicPackageDeclarations = ArrayList<EDeclaration>()
        val rootPackageDeclarations = ArrayList<EDeclaration>()
        file.declarations.forEach {
            if (isBasicPackageDeclaration(it)) {
                basicPackageDeclarations.add(it)
            } else {
                rootPackageDeclarations.add(it)
            }
        }

        val basicPackageFile = if (basicPackageDeclarations.isNotEmpty()) {
            file.declarations = basicPackageDeclarations
            file
        } else null
        val rootPackageFile = if (rootPackageDeclarations.isNotEmpty()) {
            val baseFileName = file.outputPath.fileName.toString().substringBeforeLast(".")
            val outputPath = file.outputPath.resolveSibling("$baseFileName.sv")
            EFile(
                file.location,
                file.inputPath,
                outputPath,
                rootPackageDeclarations
            )
        } else null
        return SplitResult(basicPackageFile, rootPackageFile)
    }

    private fun isBasicPackageDeclaration(declaration: EDeclaration): Boolean {
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
                "Unexpected declaration type: ${declaration::class.simpleName}"
            )
        }
    }

    data class SplitResult(val basicPackageFile: EFile?, val rootPackageFile: EFile?)
}
