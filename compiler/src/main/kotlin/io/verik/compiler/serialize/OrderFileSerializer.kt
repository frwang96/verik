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

package io.verik.compiler.serialize

import io.verik.compiler.ast.element.VkOutputFile
import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import io.verik.compiler.util.ElementUtil

object OrderFileSerializer {

    fun serialize(projectContext: ProjectContext): TextFile {
        val inputPath = projectContext.config.projectDir.resolve("src")
        val outputPath = projectContext.config.buildDir.resolve("order.yaml")
        val fileHeader = FileHeaderBuilder.build(
            projectContext,
            inputPath,
            outputPath,
            FileHeaderBuilder.CommentStyle.HASH
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("top: \"${projectContext.config.top}\"")
        builder.appendLine("order:")
        projectContext.vkFiles.forEach {
            val outputFile = ElementUtil.cast<VkOutputFile>(it)
            if (outputFile != null)
                builder.appendLine("  - ${projectContext.config.buildDir.relativize(outputFile.outputPath)}")
        }

        return TextFile(outputPath, builder.toString())
    }
}