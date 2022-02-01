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

package io.verik.compiler.ast.property

import io.verik.compiler.ast.element.expression.common.EExpression

sealed class StringEntry {

    companion object {

        fun trimIndent(entries: List<StringEntry>): List<StringEntry> {
            return trimIndent(ArrayList(entries))
        }

        private fun trimIndent(entries: ArrayList<StringEntry>): List<StringEntry> {
            // Trim leading blank lines
            while (entries.isNotEmpty()) {
                val entry = entries.first()
                if (entry is LiteralStringEntry && entry.text.isBlank()) {
                    entries.removeFirst()
                } else break
            }

            // Trim trailing blank lines
            while (entries.isNotEmpty()) {
                val entry = entries.last()
                if (entry is LiteralStringEntry && entry.text.isBlank()) {
                    entries.removeLast()
                } else break
            }

            // Remove minimal indent
            var isNewLine = true
            val indents = ArrayList<Int>()
            entries.forEach { entry ->
                @Suppress("LiftReturnOrAssignment")
                if (entry is LiteralStringEntry) {
                    if (isNewLine && entry.text.isNotBlank())
                        indents.add(entry.text.indexOfFirst { !it.isWhitespace() })
                    isNewLine = entry.text == "\n"
                } else {
                    isNewLine = false
                }
            }
            val minimalIndent = indents.minOrNull() ?: 0
            isNewLine = true
            entries.forEach { entry ->
                @Suppress("LiftReturnOrAssignment")
                if (entry is LiteralStringEntry) {
                    if (isNewLine && entry.text.isNotBlank())
                        entry.text = entry.text.substring(minimalIndent)
                    isNewLine = entry.text == "\n"
                } else {
                    isNewLine = false
                }
            }
            return entries
        }
    }
}

class LiteralStringEntry(var text: String) : StringEntry()

class ExpressionStringEntry(var expression: EExpression) : StringEntry()
