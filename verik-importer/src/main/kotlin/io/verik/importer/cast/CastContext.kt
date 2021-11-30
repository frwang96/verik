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

package io.verik.importer.cast

import io.verik.importer.lex.LexerCharStream
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode

class CastContext(
    private val lexerCharStream: LexerCharStream
) {

    fun getLocation(parseTree: ParseTree): SourceLocation {
        return when (parseTree) {
            is TerminalNode -> {
                val symbol = parseTree.symbol
                lexerCharStream.getLocation(symbol.line, symbol.charPositionInLine)
            }
            is RuleNode -> getLocation(parseTree.getChild(0))
            else -> throw IllegalArgumentException("Unrecognized parse tree type")
        }
    }
}
