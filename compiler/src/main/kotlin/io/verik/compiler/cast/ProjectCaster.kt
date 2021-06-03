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
import io.verik.compiler.common.CastUtil
import io.verik.compiler.common.ElementParentChecker
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ProjectCaster {

    fun cast(projectContext: ProjectContext) {
        m.info("Cast: Check unsupported elements", null)
        UnsupportedElementChecker.check(projectContext)
        m.flush()

        m.info("Cast: Index syntax trees", null)
        val declarationMap = DeclarationMap()
        val indexerVisitor = IndexerVisitor(projectContext, declarationMap)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        m.flush()

        m.info("Cast: Cast syntax trees", null)
        val casterVisitor = CasterVisitor(projectContext, declarationMap)
        val files = projectContext.ktFiles.mapNotNull {
            CastUtil.cast<VkFile>(it.accept(casterVisitor, Unit))
        }
        projectContext.vkFiles = files
        m.flush()

        m.info("Cast: Check parent elements", null)
        ElementParentChecker.check(projectContext)
        m.flush()

        m.info("Cast: Check file paths", null)
        FileChecker.check(projectContext)
        m.flush()

        m.info("Cast: Check import directives", null)
        ImportDirectiveChecker.check(projectContext)
        m.flush()
    }
}