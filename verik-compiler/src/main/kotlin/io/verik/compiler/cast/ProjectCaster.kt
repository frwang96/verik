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

package io.verik.compiler.cast

import io.verik.compiler.ast.element.common.EBasicPackage
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.ast.element.common.EProject
import io.verik.compiler.ast.element.common.ERootPackage
import io.verik.compiler.check.cast.ImportDirectiveChecker
import io.verik.compiler.check.cast.UnsupportedElementChecker
import io.verik.compiler.check.normalize.NormalizationChecker
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.SourceLocation
import io.verik.compiler.main.m
import java.nio.file.FileSystems

object ProjectCaster : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        UnsupportedElementChecker.accept(projectContext)
        ImportDirectiveChecker.accept(projectContext)

        m.log("Cast: Index syntax trees")
        val castContext = CastContext(projectContext.bindingContext)
        val indexerVisitor = IndexerVisitor(castContext)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        m.flush()

        m.log("Cast: Cast syntax trees")
        castSyntaxTrees(projectContext, castContext)
        NormalizationChecker.pass(projectContext)
        m.flush()
    }

    private fun castSyntaxTrees(projectContext: ProjectContext, castContext: CastContext) {
        val files = HashMap<String, ArrayList<EFile>>()
        projectContext.ktFiles.forEach {
            val fileCasterResult = FileCaster.cast(castContext, it)
            if (fileCasterResult.packageName !in files)
                files[fileCasterResult.packageName] = ArrayList()
            files[fileCasterResult.packageName]!!.add(fileCasterResult.file)
        }
        val basicPackages = ArrayList<EBasicPackage>()
        files.forEach { (packageName, files) ->
            val relativePath = packageName.replace(".", FileSystems.getDefault().separator)
            val inputPath = projectContext.config.inputSourceDir.resolve(relativePath)
            val outputPath = projectContext.config.outputSourceDir.resolve(relativePath)
            val location = SourceLocation(0, 0, inputPath, null)
            val basicPackage = EBasicPackage(location, packageName, files, inputPath, outputPath)
            basicPackages.add(basicPackage)
        }
        val projectLocation = SourceLocation(0, 0, projectContext.config.inputSourceDir, null)
        val rootPackage = ERootPackage(projectLocation, ArrayList())
        val project = EProject(projectLocation, basicPackages, rootPackage)
        projectContext.project = project
    }
}