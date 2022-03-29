/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.declaration.common.EFile
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

/**
 * Builder that builds a list of [sourceActionLines] that are used to format the contents and whitespace of a source
 * file.
 */
class SourceActionBuilder(
    private val file: EFile
) {

    private var sourceActionLine = SourceActionLine(0, ArrayList())
    private val sourceActionLines = ArrayList<SourceActionLine>()
    private val locationStack = ArrayDeque<SourceLocation>()
    private var indent = 0

    fun getSourceActionLines(): List<SourceActionLine> {
        if (sourceActionLine.sourceActions.isNotEmpty()) {
            Messages.INTERNAL_ERROR.on(file, "Serialized source must end with a new line")
        }
        return sourceActionLines
    }

    fun label(location: SourceLocation, block: () -> Unit) {
        locationStack.push(location)
        block()
        locationStack.pop()
    }

    fun indent(block: () -> Unit) {
        if (sourceActionLine.sourceActions.isNotEmpty()) {
            Messages.INTERNAL_ERROR.on(file, "Indent in must start at a new line")
        }
        sourceActionLine = SourceActionLine(++indent, ArrayList())
        block()
        if (sourceActionLine.sourceActions.isNotEmpty()) {
            Messages.INTERNAL_ERROR.on(file, "Indent out must start at a new line")
        }
        sourceActionLine = SourceActionLine(--indent, ArrayList())
    }

    fun appendLine(content: String) {
        append(content)
        appendLine()
    }

    fun appendLine() {
        sourceActionLines.add(sourceActionLine)
        sourceActionLine = SourceActionLine(indent, ArrayList())
    }

    fun append(content: String) {
        val sourceAction = SourceAction(SourceActionType.REGULAR, content, locationStack.peek()!!)
        sourceActionLine.sourceActions.add(sourceAction)
    }

    fun softBreak() {
        val sourceAction = SourceAction(SourceActionType.SOFT_BREAK, "", locationStack.peek()!!)
        sourceActionLine.sourceActions.add(sourceAction)
    }

    fun hardBreak() {
        val sourceAction = SourceAction(SourceActionType.HARD_BREAK, "", locationStack.peek()!!)
        sourceActionLine.sourceActions.add(sourceAction)
    }

    fun align() {
        val sourceAction = SourceAction(SourceActionType.ALIGN, "", locationStack.peek()!!)
        sourceActionLine.sourceActions.add(sourceAction)
    }
}
