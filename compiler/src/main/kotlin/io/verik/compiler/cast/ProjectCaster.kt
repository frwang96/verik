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

import io.verik.compiler.ast.element.VkFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m
import io.verik.compiler.normalize.ProjectNormalizationChecker

object ProjectCaster {

    fun cast(projectContext: ProjectContext) {
        m.log("Cast: Check unsupported elements")
        UnsupportedElementChecker.check(projectContext)
        m.flush()

        m.log("Cast: Index syntax trees")
        val declarationMap = DeclarationMap()
        val indexerVisitor = IndexerVisitor(projectContext, declarationMap)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        m.flush()

        m.log("Cast: Cast syntax trees")
        val baseVisitor = CasterBaseVisitor(projectContext, declarationMap)
        val files = projectContext.ktFiles.mapNotNull {
            baseVisitor.getElement<VkFile>(it)
        }
        projectContext.vkFiles = files
        m.flush()
        ProjectNormalizationChecker.check(projectContext)

        m.log("Cast: Check file paths")
        FileChecker.check(projectContext)
        m.flush()

        m.log("Cast: Check import directives")
        ImportDirectiveChecker.check(projectContext)
        m.flush()

        if (projectContext.config.debug) {
            ElementCounter.count(projectContext)
        }
    }
}