/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.interpret

import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EEnumEntry
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.declaration.common.EProperty
import io.verik.compiler.ast.element.declaration.sv.EAbstractComponent
import io.verik.compiler.ast.element.declaration.sv.ECoverGroup
import io.verik.compiler.ast.element.declaration.sv.EEnum
import io.verik.compiler.ast.element.declaration.sv.EInjectedProperty
import io.verik.compiler.ast.element.declaration.sv.EStruct
import io.verik.compiler.ast.element.declaration.sv.ESvClass
import io.verik.compiler.ast.element.declaration.sv.ESvFunction
import io.verik.compiler.ast.element.declaration.sv.ETask
import io.verik.compiler.ast.element.declaration.sv.EUnion
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

object FileSplitterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val rootPackage = projectContext.project.regularRootPackage
        projectContext.project.regularNonRootPackages.forEach { nonRootPackage ->
            val nonRootPackageFiles = ArrayList<EFile>()
            nonRootPackage.files.forEach {
                val splitResult = splitFile(it)
                if (splitResult.nonRootPackageFile != null) {
                    nonRootPackageFiles.add(splitResult.nonRootPackageFile)
                }
                if (splitResult.rootPackageFile != null) {
                    splitResult.rootPackageFile.parent = rootPackage
                    rootPackage.files.add(splitResult.rootPackageFile)
                }
            }
            nonRootPackage.files = nonRootPackageFiles
        }
    }

    private fun splitFile(file: EFile): SplitResult {
        val nonRootPackageDeclarations = ArrayList<EDeclaration>()
        val rootPackageDeclarations = ArrayList<EDeclaration>()
        file.declarations.forEach {
            if (isRootPackageDeclaration(it)) {
                rootPackageDeclarations.add(it)
            } else {
                nonRootPackageDeclarations.add(it)
            }
        }

        val nonRootPackageFile = if (nonRootPackageDeclarations.isNotEmpty()) {
            file.declarations = nonRootPackageDeclarations
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
        return SplitResult(nonRootPackageFile, rootPackageFile)
    }

    private fun isRootPackageDeclaration(declaration: EDeclaration): Boolean {
        return when (declaration) {
            is EAbstractComponent -> true
            is ESvClass -> false
            is ECoverGroup -> false
            is EEnum -> false
            is EStruct -> false
            is EUnion -> false
            is ESvFunction -> false
            is ETask -> false
            is EInjectedProperty -> false
            is EProperty -> false
            is EEnumEntry -> false
            else -> Messages.INTERNAL_ERROR.on(
                declaration,
                "Unexpected declaration type: ${declaration::class.simpleName}"
            )
        }
    }

    data class SplitResult(val nonRootPackageFile: EFile?, val rootPackageFile: EFile?)
}
