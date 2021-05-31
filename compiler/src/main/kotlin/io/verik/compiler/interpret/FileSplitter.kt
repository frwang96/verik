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

import io.verik.compiler.ast.common.QualifiedName
import io.verik.compiler.ast.common.SourceType
import io.verik.compiler.ast.element.*
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector

object FileSplitter {

    fun split(projectContext: ProjectContext) {
        val splitFiles = ArrayList<VkFile>()
        projectContext.vkFiles.forEach {
            val baseFilePath = projectContext.config.buildDir
                .resolve("src")
                .resolve(it.relativePath)
            val baseFileName = baseFilePath.fileName.toString().removeSuffix(".kt")
            val splitDeclarationsResult = splitDeclarations(it.declarations)

            if (splitDeclarationsResult.componentDeclarations.isNotEmpty()) {
                val componentFilePath = baseFilePath.resolveSibling("$baseFileName.sv")
                val componentFile = VkOutputFile(
                    it.location,
                    it.inputPath,
                    it.relativePath,
                    it.sourceSetType,
                    QualifiedName.ROOT,
                    splitDeclarationsResult.componentDeclarations,
                    componentFilePath,
                    SourceType.COMPONENT
                )
                splitFiles.add(componentFile)
            }

            if (splitDeclarationsResult.packageDeclarations.isNotEmpty()) {
                val packageFilePath = baseFilePath.resolveSibling("$baseFileName.svh")
                val packageFile = VkOutputFile(
                    it.location,
                    it.inputPath,
                    it.relativePath,
                    it.sourceSetType,
                    it.packageName,
                    splitDeclarationsResult.packageDeclarations,
                    packageFilePath,
                    SourceType.PACKAGE
                )
                splitFiles.add(packageFile)
            }
        }
        projectContext.vkFiles = splitFiles
    }

    private fun splitDeclarations(declarations: List<VkDeclaration>): SplitDeclarationsResult {
        val componentDeclarations = ArrayList<VkDeclaration>()
        val packageDeclarations = ArrayList<VkDeclaration>()
        declarations.forEach {
            when (it) {
                is VkModule -> componentDeclarations.add(it)
                is VkBaseClass -> packageDeclarations.add(it)
                is VkBaseProperty -> packageDeclarations.add(it)
                else -> messageCollector.error("Unable to identify declaration: ${it.name}", it)
            }
        }
        return SplitDeclarationsResult(componentDeclarations, packageDeclarations)
    }

    data class SplitDeclarationsResult(
        val componentDeclarations: ArrayList<VkDeclaration>,
        val packageDeclarations: ArrayList<VkDeclaration>
    )
}