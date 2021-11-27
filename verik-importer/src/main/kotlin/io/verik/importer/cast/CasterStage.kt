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

package io.verik.importer.cast

import io.verik.importer.ast.element.ECompilationUnit
import io.verik.importer.ast.element.EDeclaration
import io.verik.importer.common.ImporterStage
import io.verik.importer.main.ImporterContext
import io.verik.importer.message.SourceLocation

object CasterStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val declarations = ArrayList<EDeclaration>()
        val castContext = CastContext(importerContext.lexerCharStream)
        val casterVisitor = CasterVisitor(castContext)
        val parseTree = importerContext.parseTree
        (0 until parseTree.childCount - 1).forEach {
            val declaration = casterVisitor.getDeclaration(parseTree.getChild(it))
            if (declaration != null)
                declarations.add(declaration)
        }
        val location = SourceLocation(importerContext.config.projectDir, 0, 0)
        importerContext.compilationUnit = ECompilationUnit(location, declarations)
    }
}
