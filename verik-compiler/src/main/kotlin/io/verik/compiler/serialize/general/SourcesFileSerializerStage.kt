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

package io.verik.compiler.serialize.general

import io.verik.compiler.common.ProjectStage
import io.verik.compiler.common.TextFile
import io.verik.compiler.main.Platform
import io.verik.compiler.main.ProjectContext
import java.nio.file.Path

object SourcesFileSerializerStage : ProjectStage() {

    override val checkNormalization = false

    override fun process(projectContext: ProjectContext) {
        val outputPath = projectContext.config.buildDir.resolve("sources.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            null,
            outputPath,
            FileHeaderBuilder.HeaderStyle.YAML
        )

        val paths = ArrayList<Path>()
        projectContext.outputContext.targetPackageTextFile?.let { paths.add(it.path) }
        projectContext.outputContext.packageWrapperTextFiles.forEach { paths.add(it.path) }
        projectContext.outputContext.rootPackageTextFiles.forEach { paths.add(it.path) }

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("sources:")
        paths.forEach {
            val pathString = Platform.getStringFromPath(projectContext.config.buildDir.relativize(it))
            builder.appendLine("  - $pathString")
        }
        projectContext.outputContext.sourcesTextFile = TextFile(outputPath, builder.toString())
    }
}
