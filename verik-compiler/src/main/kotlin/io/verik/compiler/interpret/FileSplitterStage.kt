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
import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.kt.ETypeAlias
import io.verik.compiler.ast.element.sv.EAbstractComponent
import io.verik.compiler.ast.element.sv.EEnum
import io.verik.compiler.ast.element.sv.EStruct
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvEnumEntry
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.element.sv.ETask
import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages

object FileSplitterStage : ProjectStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val componentFiles = ArrayList<EFile>()
        projectContext.project.basicPackages.forEach { basicPackage ->
            val packageFiles = ArrayList<EFile>()
            basicPackage.files.forEach {
                val baseFileName = it.inputPath.fileName.toString().removeSuffix(".kt")
                val splitDeclarationsResult = splitDeclarations(it.declarations)

                if (splitDeclarationsResult.componentDeclarations.isNotEmpty()) {
                    val componentFilePath = basicPackage.outputPath.resolve("$baseFileName.sv")
                    val componentFile = EFile(
                        it.location,
                        it.inputPath,
                        componentFilePath,
                        splitDeclarationsResult.componentDeclarations
                    )
                    componentFiles.add(componentFile)
                }

                if (splitDeclarationsResult.packageDeclarations.isNotEmpty()) {
                    val packageFilePath = basicPackage.outputPath.resolve("$baseFileName.svh")
                    val packageFile = EFile(
                        it.location,
                        it.inputPath,
                        packageFilePath,
                        splitDeclarationsResult.packageDeclarations
                    )
                    packageFiles.add(packageFile)
                }
            }
            packageFiles.forEach { it.parent = basicPackage }
            basicPackage.files = packageFiles
        }
        componentFiles.forEach { it.parent = projectContext.project.rootPackage }
        projectContext.project.rootPackage.files = componentFiles
    }

    private fun splitDeclarations(declarations: List<EElement>): SplitDeclarationsResult {
        val componentDeclarations = ArrayList<EDeclaration>()
        val packageDeclarations = ArrayList<EDeclaration>()
        declarations.forEach {
            when (it) {
                is EAbstractComponent -> componentDeclarations.add(it)
                is ESvBasicClass -> packageDeclarations.add(it)
                is EEnum -> packageDeclarations.add(it)
                is EStruct -> packageDeclarations.add(it)
                is ESvFunction -> packageDeclarations.add(it)
                is ETask -> packageDeclarations.add(it)
                is ESvProperty -> packageDeclarations.add(it)
                is ESvEnumEntry -> packageDeclarations.add(it)
                is ETypeAlias -> {}
                else -> Messages.INTERNAL_ERROR.on(it, "Unable to identify as component or package declaration: $it")
            }
        }
        return SplitDeclarationsResult(componentDeclarations, packageDeclarations)
    }

    data class SplitDeclarationsResult(
        val componentDeclarations: ArrayList<EDeclaration>,
        val packageDeclarations: ArrayList<EDeclaration>
    )
}
