/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that runs the preprocessor to build preprocessor fragments.
 */
object PreprocessorStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.inputFileContexts.forEach {
            processInputFileContext(it, projectContext)
        }
    }

    private fun processInputFileContext(inputFileContext: InputFileContext, projectContext: ProjectContext) {
        val preprocessorFragments = ArrayList<PreprocessorFragment>()
        val preprocessContext = PreprocessContext(
            preprocessorFragments,
            projectContext.includedTextFiles,
            projectContext.config.includeDirs
        )
        preprocessContext.preprocess(inputFileContext.textFile)
        inputFileContext.preprocessorFragments = preprocessorFragments
    }
}
