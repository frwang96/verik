/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.pre

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages

/**
 * Check that the input source files have the Verik file annotation.
 */
object FileAnnotationCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.getKtFiles().forEach { file ->
            val isAnnotated = file.annotationEntries.any { it.shortName.toString() == "Verik" }
            if (!isAnnotated) {
                Messages.UNANNOTATED_FILE.on(file)
            }
        }
    }
}
