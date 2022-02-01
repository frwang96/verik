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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.element.declaration.common.EDeclaration
import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.ast.element.expression.common.EExpression
import io.verik.compiler.message.SourceLocation

class SerializeContext(file: EFile) {

    private val sourceSerializerVisitor = SourceSerializerVisitor(this)
    private val sourceActionBuilder = SourceActionBuilder(file)

    fun serialize(element: EElement) {
        sourceSerializerVisitor.serialize(element)
    }

    fun serializeAsDeclaration(declaration: EDeclaration) {
        sourceSerializerVisitor.serializeAsDeclaration(declaration)
    }

    fun serializeAsExpression(expression: EExpression) {
        sourceSerializerVisitor.serializeAsExpression(expression)
    }

    fun serializeAsStatement(expression: EExpression) {
        sourceSerializerVisitor.serializeAsStatement(expression)
    }

    fun <T> serializeJoin(entries: List<T>, serializer: (T) -> Unit) {
        if (entries.isNotEmpty()) {
            serializer(entries[0])
            entries.drop(1).forEach {
                sourceActionBuilder.append(",")
                sourceActionBuilder.hardBreak()
                serializer(it)
            }
        }
    }

    fun <T> serializeJoinAppendLine(entries: List<T>, serializer: (T) -> Unit) {
        if (entries.isNotEmpty()) {
            serializer(entries[0])
            entries.drop(1).forEach {
                sourceActionBuilder.appendLine(",")
                serializer(it)
            }
            sourceActionBuilder.appendLine()
        }
    }

    fun getSourceActionLines(): List<SourceActionLine> {
        return sourceActionBuilder.getSourceActionLines()
    }

    fun label(element: EElement, block: () -> Unit) {
        sourceActionBuilder.label(element.location, block)
    }

    fun label(location: SourceLocation, block: () -> Unit) {
        sourceActionBuilder.label(location, block)
    }

    fun indent(block: () -> Unit) {
        sourceActionBuilder.indent(block)
    }

    fun appendLine(content: String) {
        sourceActionBuilder.appendLine(content)
    }

    fun appendLine() {
        sourceActionBuilder.appendLine()
    }

    fun append(content: String) {
        sourceActionBuilder.append(content)
    }

    fun softBreak() {
        sourceActionBuilder.softBreak()
    }

    fun hardBreak() {
        sourceActionBuilder.hardBreak()
    }

    fun align() {
        sourceActionBuilder.align()
    }
}
