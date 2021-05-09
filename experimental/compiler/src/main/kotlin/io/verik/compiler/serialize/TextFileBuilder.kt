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

import io.verik.compiler.main.ProjectContext
import io.verik.compiler.main.TextFile
import java.nio.file.Path

open class TextFileBuilder(val projectContext: ProjectContext, val inputPath: Path, val outputPath: Path) {

    internal val sourceBuilder = StringBuilder()

    init {
        sourceBuilder.appendLine("/*")
        sourceBuilder.appendLine(" * Project: ${projectContext.config.projectName}")
        sourceBuilder.appendLine(" * Input:   ${projectContext.config.projectDir.relativize(inputPath)}")
        sourceBuilder.appendLine(" * Output:  ${projectContext.config.projectDir.relativize(outputPath)}")
        sourceBuilder.appendLine(" * Date:    ${projectContext.config.timestamp}")
        sourceBuilder.appendLine(" * Version: ${projectContext.config.version}")
        sourceBuilder.appendLine(" */")
        sourceBuilder.appendLine("")
    }

    open fun toTextFile(): TextFile {
        return TextFile(outputPath, sourceBuilder.toString())
    }
}