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

import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.common.ProjectPass
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.m

object ProjectCaster : ProjectPass {

    override fun pass(projectContext: ProjectContext) {
        m.log("Cast: Check unsupported elements")
        UnsupportedElementChecker.pass(projectContext)
        m.flush()

        m.log("Cast: Index syntax trees")
        val declarationMap = DeclarationMap(projectContext.bindingContext)
        val indexerVisitor = IndexerVisitor(projectContext, declarationMap)
        projectContext.ktFiles.forEach { it.accept(indexerVisitor) }
        m.flush()

        m.log("Cast: Cast syntax trees")
        val baseCasterVisitor = BaseCasterVisitor(projectContext, declarationMap)
        val files = projectContext.ktFiles.mapNotNull {
            baseCasterVisitor.getElement<EFile>(it)
        }
        projectContext.files = files
        m.flush()

        if (projectContext.config.debug) {
            ElementCounter.pass(projectContext)
        }
    }
}