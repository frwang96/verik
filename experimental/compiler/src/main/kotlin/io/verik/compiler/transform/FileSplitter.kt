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

package io.verik.compiler.transform

import io.verik.compiler.ast.SourceType
import io.verik.compiler.ast.VkFile
import io.verik.compiler.ast.VkOutputFile
import io.verik.compiler.main.ProjectContext

object FileSplitter {

    fun split(projectContext: ProjectContext) {
        val splitFiles = ArrayList<VkFile>()
        projectContext.vkFiles.forEach {
            val baseFilePath = projectContext.config.buildDir
                .resolve("src")
                .resolve(it.relativePath)
            val baseFileName = baseFilePath.fileName.toString().removeSuffix(".kt")

            val componentFilePath = baseFilePath.resolveSibling("$baseFileName.sv")
            val componentFile = VkOutputFile(
                it.location,
                it.inputPath,
                it.relativePath,
                it.sourceSetType,
                ArrayList(),
                componentFilePath,
                SourceType.COMPONENT
            )
            splitFiles.add(componentFile)

            val packageFilePath = baseFilePath.resolveSibling("$baseFileName.svh")
            val packageFile = VkOutputFile(
                it.location,
                it.inputPath,
                it.relativePath,
                it.sourceSetType,
                it.declarations,
                packageFilePath,
                SourceType.PACKAGE
            )
            splitFiles.add(packageFile)
        }
        projectContext.vkFiles = splitFiles
    }
}