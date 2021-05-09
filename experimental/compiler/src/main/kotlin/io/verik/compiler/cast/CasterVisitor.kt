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

import io.verik.compiler.ast.*
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector
import io.verik.compiler.util.ElementUtil
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtVisitor
import java.nio.file.Paths

class CasterVisitor(projectContext: ProjectContext): KtVisitor<VkElement, Unit>() {

    private val mainPath = projectContext.config.projectDir.resolve("src/main/kotlin")
    private val testPath = projectContext.config.projectDir.resolve("src/test/kotlin")

    override fun visitKtFile(file: KtFile, data: Unit): VkElement? {
        val location = CasterUtil.getMessageLocation(file)
        val inputPath = Paths.get(file.virtualFilePath)
        val (sourceSetType, relativePath) = when {
            inputPath.startsWith(mainPath) -> Pair(SourceSetType.MAIN, mainPath.relativize(inputPath))
            inputPath.startsWith(testPath) -> Pair(SourceSetType.TEST, testPath.relativize(inputPath))
            else -> {
                messageCollector.error("Unable to identify as main or test source", location)
                return null
            }
        }
        val pathPackageName = (0 until (relativePath.nameCount - 1))
            .joinToString(separator = ".") { relativePath.getName(it).toString() }
            .let { if (it != "") Name(it) else Name.ROOT }
        val packageName = Name(file.packageFqName.toString())
        if (packageName != pathPackageName)
            messageCollector.error("Package directive does not match file location", location)
        if (packageName == Name.ROOT)
            messageCollector.error("Use of the root package is prohibited", location)

        val declarations = file.declarations.mapNotNull { ElementUtil.cast<VkDeclaration>(it.accept(this, Unit)) }
        return VkFile(location, inputPath, relativePath, sourceSetType, packageName)
            .also { it.declarations = ArrayList(declarations) }
    }

    override fun visitClassOrObject(classOrObject: KtClassOrObject, data: Unit?): VkElement {
        val name = Name(classOrObject.nameAsSafeName.identifier)
        val location = CasterUtil.getMessageLocation(classOrObject)
        return VkClass(name, location)
    }
}