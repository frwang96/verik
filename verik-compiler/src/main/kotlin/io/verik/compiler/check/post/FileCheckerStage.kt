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

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.message.Messages
import java.nio.file.Path
import java.nio.file.Paths

object FileCheckerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        projectContext.project.basicPackages.forEach {
            if (it.name == "verik_pkg")
                Messages.PACKAGE_NAME_RESERVED.on(it, it.name)
        }

        val pathSet = HashSet<Path>()
        val duplicatedPathSet = HashSet<Path>()
        projectContext.project.files().forEach {
            val inputPath = it.inputPath
            if (inputPath.fileName == Paths.get("Pkg.kt"))
                Messages.FILE_NAME_RESERVED.on(it, inputPath.fileName)

            val outputPath = it.getOutputPathNotNull()
            if (outputPath in pathSet)
                duplicatedPathSet.add(outputPath)
            else
                pathSet.add(outputPath)
        }

        if (duplicatedPathSet.isNotEmpty()) {
            projectContext.project.files().forEach {
                val outputPath = it.getOutputPathNotNull()
                if (outputPath in duplicatedPathSet)
                    Messages.FILE_NAME_DUPLICATED.on(it, it.inputPath.fileName)
            }
        }
    }
}
