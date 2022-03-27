/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.parse

import io.verik.importer.antlr.SystemVerilogParser
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.WritableToken

object ParserFilter {

    fun filter(tokens: List<WritableToken>) {
        tokens.forEachIndexed { index, token ->
            when (token.type) {
                SystemVerilogParser.ENDFUNCTION -> filterFunction(tokens, index)
                SystemVerilogParser.ENDTASK -> filterTask(tokens, index)
            }
        }
    }

    private fun filterFunction(tokens: List<WritableToken>, index: Int) {
        val functionIndex = findPrevious(tokens, index, SystemVerilogParser.FUNCTION) ?: return
        val semicolonIndex = findNextSemicolon(tokens, functionIndex) ?: return
        val rparenIndex = findPrevious(tokens, semicolonIndex, SystemVerilogParser.RPAREN) ?: return
        if (rparenIndex in functionIndex..semicolonIndex) {
            hide(tokens, semicolonIndex + 1, index - 1)
        }
    }

    private fun filterTask(tokens: List<WritableToken>, index: Int) {
        val taskIndex = findPrevious(tokens, index, SystemVerilogParser.TASK) ?: return
        val semicolonIndex = findNextSemicolon(tokens, taskIndex) ?: return
        val rparenIndex = findPrevious(tokens, semicolonIndex, SystemVerilogParser.RPAREN) ?: return
        if (rparenIndex in taskIndex..semicolonIndex) {
            hide(tokens, semicolonIndex + 1, index - 1)
        }
    }

    private fun findPrevious(tokens: List<WritableToken>, index: Int, type: Int): Int? {
        for (previousIndex in index downTo 0) {
            if (tokens[previousIndex].type == type) return previousIndex
        }
        return null
    }

    private fun findNextSemicolon(tokens: List<WritableToken>, index: Int): Int? {
        for (nextIndex in index until tokens.size) {
            if (tokens[nextIndex].type == SystemVerilogParser.SEMICOLON) return nextIndex
        }
        return null
    }

    private fun hide(tokens: List<WritableToken>, startIndex: Int, endIndex: Int) {
        if (endIndex > startIndex) {
            for (index in startIndex..endIndex) {
                tokens[index].channel = Token.HIDDEN_CHANNEL
            }
        }
    }
}
