/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.cast.common

import io.verik.importer.ast.element.common.EProject
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that casts ANTLR rule contexts into a [project element][EProject].
 */
object CasterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        val declarations = ArrayList<EDeclaration>()
        projectContext.inputFileContexts.forEach {
            declarations.addAll(castDeclarations(it))
        }
        projectContext.project = EProject(declarations)
    }

    private fun castDeclarations(inputFileContext: InputFileContext): List<EDeclaration> {
        val castContext = CastContext(inputFileContext.parserTokenStream)
        return inputFileContext.ruleContext.description().flatMap { castContext.castDeclarations(it) }
    }
}
