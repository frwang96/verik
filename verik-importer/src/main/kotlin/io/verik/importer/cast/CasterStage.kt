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
import io.verik.importer.ast.element.ERootPackage
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.SourceLocation

object CasterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarations = ArrayList<EDeclaration>()
        val castContext = CastContext(projectContext.parserTokenStream)
        val casterVisitor = CasterVisitor(castContext)
        val parseTree = projectContext.parseTree
        (0 until parseTree.childCount - 1).forEach {
            val declaration = casterVisitor.getDeclaration(parseTree.getChild(it))
            if (declaration != null)
                declarations.add(declaration)
        }
        val rootPackage = ERootPackage(SourceLocation.NULL, declarations)
        projectContext.compilationUnit = ECompilationUnit(SourceLocation.NULL, rootPackage)
    }
}
