/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that filters out comments and white spaces from the preprocessor fragments.
 */
object PreprocessorFilterStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.inputFileContexts.forEach {
            processInputFileContext(it)
        }
    }

    private fun processInputFileContext(inputFileContext: InputFileContext) {
        val preprocessorFragments = inputFileContext.preprocessorFragments.filter {
            !isCommentOrWhitespace(it)
        }
        inputFileContext.preprocessorFragments = ArrayList(preprocessorFragments)
    }

    private fun isCommentOrWhitespace(preprocessorFragment: PreprocessorFragment): Boolean {
        val content = preprocessorFragment.content
        return when {
            content.startsWith("/*") -> true
            content.startsWith("//") -> true
            content.isBlank() -> true
            else -> false
        }
    }
}
