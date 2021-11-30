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

package io.verik.importer.parse

import io.verik.importer.lex.LexerFragment
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.TokenFactory
import org.antlr.v4.runtime.TokenSource
import org.antlr.v4.runtime.misc.Pair

class ParserTokenFactory(
    private val parserTokenSource: ParserTokenSource
) : TokenFactory<ParserToken> {

    fun create(lexerFragment: LexerFragment, index: Int): ParserToken {
        return ParserToken(
            lexerFragment.virtualLine,
            lexerFragment.virtualColumn,
            lexerFragment.type,
            lexerFragment.content,
            index,
            parserTokenSource
        )
    }

    override fun create(
        source: Pair<TokenSource, CharStream>?,
        type: Int,
        text: String?,
        channel: Int,
        start: Int,
        stop: Int,
        line: Int,
        charPositionInLine: Int
    ): ParserToken {
        return ParserToken(line, charPositionInLine, type, text!!, -1, parserTokenSource)
    }

    override fun create(type: Int, text: String?): ParserToken {
        return ParserToken(0, 0, type, text!!, -1, parserTokenSource)
    }
}
