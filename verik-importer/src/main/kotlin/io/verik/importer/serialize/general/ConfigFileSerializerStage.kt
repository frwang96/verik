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

package io.verik.importer.serialize.general

import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import io.verik.importer.main.ImporterStage
import io.verik.importer.main.Platform

object ConfigFileSerializerStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val outputPath = importerContext.config.buildDir.resolve("config.yaml")
        val fileHeader = FileHeaderBuilder.build(
            importerContext,
            outputPath,
            FileHeaderBuilder.HeaderStyle.YAML
        )

        val builder = StringBuilder()
        builder.append(fileHeader)
        builder.appendLine("importedFiles:")
        importerContext.config.importedFiles.forEach {
            builder.appendLine("  - ${Platform.getStringFromPath(it.toAbsolutePath())}")
        }
        importerContext.outputContext.configTextFile = TextFile(outputPath, builder.toString())
    }
}
