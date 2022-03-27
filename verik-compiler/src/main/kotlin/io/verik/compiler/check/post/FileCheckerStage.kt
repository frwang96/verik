/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.check.post

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import java.nio.file.Path

object FileCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.packages().forEach {
            if (it.name == "verik_pkg") {
                Messages.RESERVED_PACKAGE_NAME.on(it, it.name)
            }
        }

        val pathSet = HashSet<Path>()
        val duplicatedPathSet = HashSet<Path>()
        projectContext.project.regularFiles().forEach {
            val outputPath = it.outputPath
            if (outputPath in pathSet) {
                duplicatedPathSet.add(outputPath)
            } else {
                pathSet.add(outputPath)
            }
        }

        if (duplicatedPathSet.isNotEmpty()) {
            projectContext.project.regularFiles().forEach {
                if (it.outputPath in duplicatedPathSet) {
                    Messages.DUPLICATED_FILE_NAME.on(it, it.inputPath.fileName)
                }
            }
        }
    }
}
