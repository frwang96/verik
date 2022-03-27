/*
 * SPDX-License-Identifier: Apache-2.0
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
