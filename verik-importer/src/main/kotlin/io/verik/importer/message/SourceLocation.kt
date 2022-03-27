/*
 * SPDX-License-Identifier: Apache-2.0
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
