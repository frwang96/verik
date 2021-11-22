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
import io.verik.compiler.ast.element.common.EExpression
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.main.ProjectContext

class SerializerContext(projectContext: ProjectContext, file: EFile) {

    private val sourceSerializerVisitor = SourceSerializerVisitor(this)
    private val sourceBuilder = SourceBuilder(projectContext, file)

    fun serialize(element: EElement) {
        sourceSerializerVisitor.serialize(element)
    }

    fun serializeAsDeclaration(element: EElement) {
        sourceSerializerVisitor.serializeAsDeclaration(element)
    }

    fun serializeAsExpression(expression: EExpression) {
        sourceSerializerVisitor.serializeAsExpression(expression)
    }

    fun serializeAsStatement(expression: EExpression) {
        sourceSerializerVisitor.serializeAsStatement(expression)
    }

    fun getSourceBuilderResult(): SourceBuilderResult {
        return sourceBuilder.getResult()
    }

    fun label(element: EElement, block: () -> Unit) {
        sourceBuilder.label(element, block)
    }

    fun indent(block: () -> Unit) {
        sourceBuilder.indent(block)
    }

    fun appendLine(content: String) {
        sourceBuilder.appendLine(content)
    }

    fun appendLine() {
        sourceBuilder.appendLine()
    }

    fun append(content: String) {
        sourceBuilder.append(content)
    }

    fun softBreak() {
        sourceBuilder.softBreak()
    }

    fun hardBreak() {
        sourceBuilder.hardBreak()
    }

    fun align() {
        sourceBuilder.align()
    }

    fun <E> join(elements: List<E>, block: (E) -> Unit) {
        if (elements.isNotEmpty()) {
            block(elements[0])
            elements.drop(1).forEach {
                sourceBuilder.append(",")
                sourceBuilder.hardBreak()
                block(it)
            }
        }
    }

    fun <E> joinLine(elements: List<E>, block: (E) -> Unit) {
        if (elements.isNotEmpty()) {
            block(elements[0])
            elements.drop(1).forEach {
                sourceBuilder.appendLine(",")
                block(it)
            }
            sourceBuilder.appendLine()
        }
    }
}
