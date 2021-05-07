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

import io.verik.compiler.ast.Name
import io.verik.compiler.ast.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import java.nio.file.Path

object ProjectCaster {

    fun cast(projectContext: ProjectContext) {
        val files = projectContext.ktFiles.map {
            it.accept(CasterVisitor, Unit) as VkFile
        }
        checkPackageNames(files, projectContext)
        projectContext.vkFiles = files
        messageCollector.flush()
    }

    private fun checkPackageNames(files: List<VkFile>, projectContext: ProjectContext) {
        val mainPackageNameSet = HashSet<Name>()
        val testPackageNameSet = HashSet<Name>()
        files.forEach {
            if (it.packageName == Name.ROOT)
                messageCollector.error("Use of the root package is prohibited", it)
            val pathResult = getPathResult(it, projectContext)
            if (pathResult != null) {
                if (it.packageName != pathResult.packageName)
                    messageCollector.error("Package directive does not match file location", it)
                if (pathResult.isMain) mainPackageNameSet.add(pathResult.packageName)
                else testPackageNameSet.add(pathResult.packageName)
            }
        }

        mainPackageNameSet.intersect(testPackageNameSet).forEach {
            messageCollector.error("Main and test packages must be distinct: $it")
        }
    }

    private fun getPathResult(file: VkFile, projectContext: ProjectContext): PathResult? {
        val path = file.path
        val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
        val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")
        return when {
            path.startsWith(mainPath) -> PathResult(true, getPackageName(mainPath.relativize(path)))
            path.startsWith(testPath) -> PathResult(false, getPackageName(testPath.relativize(path)))
            else -> {
                messageCollector.error("Unable to identify as main or test source", file)
                return null
            }
        }
    }

    private fun getPackageName(path: Path): Name {
        val packageName = (0 until (path.nameCount - 1))
            .joinToString(separator = ".") { path.getName(it).toString() }
        return if (packageName != "") Name(packageName)
        else Name.ROOT
    }

    data class PathResult(val isMain: Boolean, val packageName: Name)
}