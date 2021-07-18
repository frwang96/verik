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

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.sv.EModule
import io.verik.compiler.ast.element.sv.ESvBasicClass
import io.verik.compiler.ast.element.sv.ESvFunction
import io.verik.compiler.ast.element.sv.ESvProperty
import io.verik.compiler.ast.property.SourceType
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object FileSplitter : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        val splitFiles = ArrayList<EFile>()
        projectContext.files.forEach {
            val baseFilePath = projectContext.config.buildDir
                .resolve("src")
                .resolve(it.relativePath)
            val baseFileName = baseFilePath.fileName.toString().removeSuffix(".kt")
            val splitMemberResult = splitMembers(it.members)

            if (splitMemberResult.componentMembers.isNotEmpty()) {
                val componentFilePath = baseFilePath.resolveSibling("$baseFileName.sv")
                val componentFile = EFile(
                    it.location,
                    it.inputPath,
                    componentFilePath,
                    it.relativePath,
                    it.sourceSetType,
                    SourceType.COMPONENT,
                    CorePackage.ROOT,
                    splitMemberResult.componentMembers,
                    listOf()
                )
                splitFiles.add(componentFile)
            }

            if (splitMemberResult.packageMembers.isNotEmpty()) {
                val packageFilePath = baseFilePath.resolveSibling("$baseFileName.svh")
                val packageFile = EFile(
                    it.location,
                    it.inputPath,
                    packageFilePath,
                    it.relativePath,
                    it.sourceSetType,
                    SourceType.PACKAGE,
                    it.packageDeclaration,
                    splitMemberResult.packageMembers,
                    listOf()
                )
                splitFiles.add(packageFile)
            }
        }
        projectContext.files = splitFiles
    }

    private fun splitMembers(members: List<EElement>): SplitMembersResult {
        val componentMembers = ArrayList<EElement>()
        val packageMembers = ArrayList<EElement>()
        members.forEach {
            when (it) {
                is EModule -> componentMembers.add(it)
                is ESvBasicClass -> packageMembers.add(it)
                is ESvFunction -> packageMembers.add(it)
                is ESvProperty -> packageMembers.add(it)
                else -> m.error("Unable to identify as component or package member: ${it::class.simpleName}", it)
            }
        }
        return SplitMembersResult(componentMembers, packageMembers)
    }

    data class SplitMembersResult(
        val componentMembers: ArrayList<EElement>,
        val packageMembers: ArrayList<EElement>
    )
}