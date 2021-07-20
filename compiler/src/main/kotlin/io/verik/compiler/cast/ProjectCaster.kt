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
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.core.common.CorePackage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.SourceLocation
import io.verik.compiler.main.m

object ProjectCaster : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        m.log("Cast: Check unsupported elements")
        UnsupportedElementChecker.pass(projectContext)
        m.flush()

        m.log("Cast: Check import directives")
        ImportDirectiveChecker.pass(projectContext)
        m.flush()

        m.log("Cast: Index syntax trees")
        val castContext = CastContext(projectContext.bindingContext)
        val indexerVisitor = IndexerVisitor(projectContext, castContext)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        m.flush()

        m.log("Cast: Cast syntax trees")
        val files = HashMap<String, ArrayList<EFile>>()
        projectContext.ktFiles.forEach {
            val fileCasterResult = FileCaster.cast(projectContext, castContext, it)
            if (fileCasterResult != null) {
                if (fileCasterResult.packageName !in files)
                    files[fileCasterResult.packageName] = ArrayList()
                files[fileCasterResult.packageName]!!.add(fileCasterResult.file)
            }
        }
        val basicPackages = ArrayList<EBasicPackage>()
        files.forEach { (packageName, files) ->
            val location = SourceLocation(0, 0, files[0].inputPath.parent, null)
            basicPackages.add(EBasicPackage(location, packageName, files))
        }
        val projectLocation = SourceLocation(0, 0, projectContext.config.mainDir, null)
        val rootPackage = EBasicPackage(projectLocation, CorePackage.ROOT.name, ArrayList())
        val project = EProject(projectLocation, basicPackages, rootPackage)
        projectContext.project = project
        m.flush()

        if (projectContext.config.debug) {
            ElementCounter.pass(projectContext)
        }
    }
}