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
import io.verik.compiler.common.location
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.SourceLocation
import java.nio.file.FileSystems
import java.nio.file.Paths

object ProjectCaster : CasterStage() {

    override val checkNormalization = true

    override fun process(projectContext: ProjectContext) {
        val files = HashMap<String, ArrayList<EFile>>()

        val baseCasterVisitor = BaseCasterVisitor(projectContext.castContext)
        projectContext.ktFiles.forEach { file ->
            val location = file.location()
            val packageName = file.packageFqName.asString()
            val inputPath = Paths.get(file.virtualFilePath)
            val members = file.declarations.map { it.accept(baseCasterVisitor, Unit) }
            if (packageName !in files)
                files[packageName] = ArrayList()
            files[packageName]!!.add(EFile(location, inputPath, null, ArrayList(members)))
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