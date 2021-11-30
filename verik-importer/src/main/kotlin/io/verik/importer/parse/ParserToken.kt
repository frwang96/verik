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

import org.antlr.v4.runtime.CharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.TokenSource

class ParserToken(
    private val virtualLine: Int,
    private val virtualColumn: Int,
    private val type: Int,
    private val content: String,
    private val index: Int,
    private val tokenSource: TokenSource
) : Token {

    override fun getText(): String {
        return content
    }

    override fun getType(): Int {
        return type
    }

    override fun getLine(): Int {
        return virtualLine
    }

    override fun getCharPositionInLine(): Int {
        return virtualColumn
    }

    override fun getChannel(): Int {
        return Token.DEFAULT_CHANNEL
    }

    override fun getTokenIndex(): Int {
        return index
    }

    override fun getStartIndex(): Int {
        return -1
    }

    override fun getStopIndex(): Int {
        return -1
    }

    override fun getTokenSource(): TokenSource {
        return tokenSource
    }

    override fun getInputStream(): CharStream? {
        return tokenSource.inputStream
    }
}
