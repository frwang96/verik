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

package io.verik.importer.serialize.source

import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import io.verik.importer.serialize.general.FileHeaderBuilder
import java.nio.file.Path

class SourceBuilder(
    private val importerContext: ImporterContext,
    private val path: Path
) {

    private val builder = StringBuilder()

    private val suppressedInspections = listOf(
        "LongLine"
    )

    init {
        val fileHeader = FileHeaderBuilder.build(
            importerContext,
            path,
            FileHeaderBuilder.HeaderStyle.KOTLIN
        )
        builder.append(fileHeader)
        buildHeader()
    }

    fun getTextFile(): TextFile {
        return TextFile(path, builder.toString())
    }

    private fun buildHeader() {
        val suppressedInspectionsString = suppressedInspections.joinToString { "\"$it\"" }
        builder.appendLine("@file:Suppress($suppressedInspectionsString)")
        builder.appendLine()
        builder.appendLine("package imported")
        builder.appendLine()
    }
}
