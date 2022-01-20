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

package io.verik.importer.message

import io.verik.importer.parse.ParserCharStream
import io.verik.importer.preprocess.PreprocessorCharStream
import org.antlr.v4.runtime.Token
import org.antlr.v4.runtime.tree.TerminalNode
import java.nio.file.Path

data class SourceLocation(
    val path: Path,
    val line: Int,
    val column: Int
) {

    companion object {

        val NULL = SourceLocation(Path.of("."), 0, 0)

        fun get(terminalNode: TerminalNode): SourceLocation {
            return get(terminalNode.symbol)
        }

        fun get(token: Token): SourceLocation {
            return when (val inputStream = token.inputStream) {
                is PreprocessorCharStream -> {
                    if (inputStream.isOriginal) {
                        SourceLocation(
                            inputStream.location.path,
                            token.line,
                            token.charPositionInLine + 1
                        )
                    } else {
                        inputStream.location
                    }
                }
                is ParserCharStream -> {
                    inputStream.getLocation(token.line, token.charPositionInLine)
                }
                else -> throw IllegalArgumentException(
                    "Could not get location of token ${inputStream::class.qualifiedName}"
                )
            }
        }
    }
}
