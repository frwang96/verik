/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage

/**
 * Stage that serializes the output of the preprocessor. This stage is run before whitespaces and comments are removed
 * in [PreprocessorFilterStage].
 */
object PreprocessorSerializerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        if (projectContext.config.enablePreprocessorOutput) {
            val builder = StringBuilder()
            projectContext.inputFileContexts.forEach { inputFileContext ->
                inputFileContext.preprocessorFragments.forEach {
                    builder.append(it.content)
                }
            }
            val preprocessorTextFile = TextFile(
                projectContext.config.buildDir.resolve("imported.sv"),
                builder.toString()
            )
            projectContext.outputContext.preprocessorTextFile = preprocessorTextFile
        }
    }
}
