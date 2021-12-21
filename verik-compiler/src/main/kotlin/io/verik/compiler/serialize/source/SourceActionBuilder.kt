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
import io.verik.compiler.ast.element.common.EFile
import io.verik.compiler.message.Messages
import io.verik.compiler.message.SourceLocation
import org.jetbrains.kotlin.backend.common.peek
import org.jetbrains.kotlin.backend.common.pop
import org.jetbrains.kotlin.backend.common.push

class SourceActionBuilder(
    private val file: EFile
) {

    private var sourceActionLine = SourceActionLine(0, ArrayList())
    private val sourceActionLines = ArrayList<SourceActionLine>()
    private val locationStack = ArrayDeque<SourceLocation>()
    private var indent = 0

    fun getSourceActionLines(): List<SourceActionLine> {
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Serialized source must end with a new line")
        return sourceActionLines
    }

    fun label(element: EElement, block: () -> Unit) {
        locationStack.push(element.location)
        block()
        locationStack.pop()
    }

    fun indent(block: () -> Unit) {
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Indent in must start at a new line")
        sourceActionLine = SourceActionLine(++indent, ArrayList())
        block()
        if (sourceActionLine.sourceActions.isNotEmpty())
            Messages.INTERNAL_ERROR.on(file, "Indent out must start at a new line")
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
