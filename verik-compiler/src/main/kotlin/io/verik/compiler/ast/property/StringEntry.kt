/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.compiler.ast.property

import io.verik.compiler.ast.element.expression.common.EExpression

/**
 * Entry of a string entry expression. It can either hold an expression or a string literal.
 */
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

/**
 * String entry that holds a string literal.
 */
class LiteralStringEntry(var text: String) : StringEntry()

/**
 * String entry that holds an expression.
 */
class ExpressionStringEntry(var expression: EExpression) : StringEntry()
