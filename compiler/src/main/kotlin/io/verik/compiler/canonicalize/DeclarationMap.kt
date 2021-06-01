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

package io.verik.compiler.canonicalize

import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.messageCollector

class DeclarationMap(val projectContext: ProjectContext) {

    private val declarationMap = HashMap<VkDeclaration, VkDeclaration?>()

    init {
        projectContext.vkFiles.forEach { file ->
            file.declarations.forEach {
                declarationMap[it] = null
            }
        }
    }

    fun add(declaration: VkDeclaration, canonicalizedDeclaration: VkDeclaration) {
        if (declaration !in declarationMap)
            messageCollector.fatal("Declaration not in declaration map", declaration)
        declarationMap[declaration] = canonicalizedDeclaration
    }

    fun flush() {
        projectContext.vkFiles = projectContext.vkFiles.mapNotNull { flushFile(it) }
    }

    private fun flushFile(file: VkFile): VkFile? {
        val declarations = ArrayList<VkDeclaration>()
        file.declarations.forEach {
            val declaration = declarationMap[it]
            if (declaration != null) {
                declarations.add(declaration)
            }
        }
        return if (declarations.isNotEmpty()) {
            VkFile(
                file.location,
                file.inputPath,
                file.relativePath,
                file.sourceSetType,
                file.packageName,
                declarations,
                listOf()
            )
        } else null
    }
}