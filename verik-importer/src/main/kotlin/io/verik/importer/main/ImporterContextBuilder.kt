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

package io.verik.importer.main

import java.nio.file.Files

object ImporterContextBuilder {

    fun buildContext(config: VerikImporterConfig): ImporterContext {
        val importerContext = ImporterContext(config)
        readImportedFiles(importerContext)
        return importerContext
    }

    private fun readImportedFiles(importerContext: ImporterContext) {
        importerContext.config.importedFiles.forEach {
            if (it in importerContext.inputFileContexts)
                throw IllegalArgumentException("Duplicated file: ${it.fileName}")
            val lines = Files.readAllLines(it)
            val inputFileContext = InputFileContext(lines.joinToString(separator = "\n", postfix = "\n"))
            importerContext.inputFileContexts[it] = inputFileContext
        }
    }
}
