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

import io.verik.importer.ast.element.EElement
import io.verik.importer.common.TextFile
import io.verik.importer.main.ImporterContext
import java.nio.file.Path

class SerializerContext(
    importerContext: ImporterContext,
    packageName: String,
    path: Path
) {

    val labelSourceLocations = importerContext.config.labelSourceLocations

    private val sourceSerializerVisitor = SourceSerializerVisitor(this)
    private val sourceBuilder = SourceBuilder(importerContext, packageName, path)

    fun serialize(element: EElement) {
        element.accept(sourceSerializerVisitor)
    }

    fun <T> serializeJoinAppendLine(entries: List<T>, serializer: (T) -> Unit) {
        if (entries.isNotEmpty()) {
            serializer(entries[0])
            entries.drop(1).forEach {
                sourceBuilder.appendLine(",")
                serializer(it)
            }
            sourceBuilder.appendLine()
        }
    }

    fun getTextFile(): TextFile {
        return sourceBuilder.getTextFile()
    }

    fun indent(block: () -> Unit) {
        sourceBuilder.indent(block)
    }

    fun append(content: String) {
        sourceBuilder.append(content)
    }

    fun appendLine(content: String) {
        sourceBuilder.appendLine(content)
    }

    fun appendLine() {
        sourceBuilder.appendLine()
    }
}
