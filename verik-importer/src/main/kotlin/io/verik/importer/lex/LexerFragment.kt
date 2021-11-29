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

package io.verik.importer.lex

import org.antlr.v4.runtime.Token

data class LexerFragment(
    val virtualLine: Int,
    val virtualColumn: Int,
    val type: Int,
    val content: String
) {

    companion object {

        operator fun invoke(token: Token): LexerFragment {
            return LexerFragment(token.line, token.charPositionInLine, token.type, token.text)
        }
    }
}
