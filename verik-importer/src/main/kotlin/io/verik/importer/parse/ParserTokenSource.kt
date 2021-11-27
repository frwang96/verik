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

import io.verik.importer.common.LexerFragment
import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.IntStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenFactory
import org.antlr.v4.runtime.TokenSource

class ParserTokenSource(
    private val lexerFragments: ArrayList<LexerFragment>
) : TokenSource {

    private var index = 0

    override fun nextToken(): Token {
        val token = ParserToken(lexerFragments[index])
        index++
        return token
    }

    override fun getLine(): Int {
        return lexerFragments[index].virtualLine
    }

    override fun getCharPositionInLine(): Int {
        return lexerFragments[index].virtualColumn
    }

    override fun getInputStream(): CharStream? {
        return null
    }

    override fun getSourceName(): String {
        return IntStream.UNKNOWN_SOURCE_NAME
    }

    override fun setTokenFactory(p0: TokenFactory<*>?) {}

    override fun getTokenFactory(): TokenFactory<*>? {
        return null
    }
}
