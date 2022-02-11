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

import io.verik.importer.ast.element.common.EElement
import io.verik.importer.ast.element.declaration.EDeclaration
import io.verik.importer.common.TextFile
import io.verik.importer.main.ProjectContext
import java.nio.file.Path

class SerializeContext(
    projectContext: ProjectContext,
    packageName: String,
    outputPath: Path
) {

    private val sourceSerializerVisitor = SourceSerializerVisitor(this)
    private val sourceBuilder = SourceBuilder(projectContext, packageName, outputPath)

    fun serialize(element: EElement) {
        element.accept(sourceSerializerVisitor)
    }

    fun serializeName(declaration: EDeclaration) {
        NameSerializer.serializeName(declaration, this)
    }

    fun <E : EElement> serializeJoin(entries: List<E>) {
        if (entries.isNotEmpty()) {
            serialize(entries[0])
            entries.drop(1).forEach {
                sourceBuilder.append(", ")
                serialize(it)
            }
        }
    }

    fun <E : EElement> serializeJoinAppendLine(entries: List<E>) {
        if (entries.isNotEmpty()) {
            serialize(entries[0])
            entries.drop(1).forEach {
                sourceBuilder.appendLine(",")
                serialize(it)
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
