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
import org.antlr.v4.runtime.TokenStream
import org.antlr.v4.runtime.tree.ParseTree

class CastContext(
    private val lexerCharStream: LexerCharStream,
    private val parserTokenStream: TokenStream
) {

    fun getLocation(parseTree: ParseTree): SourceLocation {
        val index = parseTree.sourceInterval.a
        val token = parserTokenStream.get(index)
        return lexerCharStream.getLocation(token.line, token.charPositionInLine)
    }
}
