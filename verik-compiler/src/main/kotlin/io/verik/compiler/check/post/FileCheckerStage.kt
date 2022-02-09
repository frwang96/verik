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

package io.verik.compiler.check.post

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.ProjectStage
import io.verik.compiler.message.Messages
import java.nio.file.Path

object FileCheckerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.project.packages().forEach {
            if (it.name == "verik_pkg")
                Messages.RESERVED_PACKAGE_NAME.on(it, it.name)
        }

        val pathSet = HashSet<Path>()
        val duplicatedPathSet = HashSet<Path>()
        projectContext.project.regularFiles().forEach {
            val outputPath = it.outputPath
            if (outputPath.fileName.toString() == "Pkg.sv")
                Messages.RESERVED_FILE_NAME.on(it, it.inputPath.fileName)
            if (outputPath in pathSet)
                duplicatedPathSet.add(outputPath)
            else
                pathSet.add(outputPath)
        }

        if (duplicatedPathSet.isNotEmpty()) {
            projectContext.project.regularFiles().forEach {
                if (it.outputPath in duplicatedPathSet)
                    Messages.DUPLICATED_FILE_NAME.on(it, it.inputPath.fileName)
            }
        }
    }
}
