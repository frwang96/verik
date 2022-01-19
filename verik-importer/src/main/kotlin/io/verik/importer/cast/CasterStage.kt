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

import io.verik.importer.ast.sv.element.SvCompilationUnit
import io.verik.importer.ast.sv.element.SvDeclaration
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

object CasterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarations = ArrayList<SvDeclaration>()
        projectContext.inputFileContexts.forEach {
            declarations.addAll(castDeclarations(it))
        }
        projectContext.compilationUnit = SvCompilationUnit(declarations)
    }

    private fun castDeclarations(inputFileContext: InputFileContext): List<SvDeclaration> {
        val declarations = ArrayList<SvDeclaration>()
        val castContext = CastContext(inputFileContext.parserTokenStream)
        inputFileContext.ruleContext.description().forEach {
            val declaration = castContext.getDeclaration(it)
            if (declaration != null)
                declarations.add(declaration)
        }
        return declarations
    }
}
