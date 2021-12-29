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

package io.verik.importer.preprocess

import io.verik.importer.antlr.preprocess.MacroContentLexer
import io.verik.importer.message.Messages
import io.verik.importer.message.RecognitionExceptionFormatter
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token

object MacroEvaluator {

    fun tokenize(content: String, location: SourceLocation): List<Token> {
        val charStream = CharStreams.fromString(content)
        val lexer = MacroContentLexer(charStream)
        val lexerErrorListener = LexerErrorListener(location)
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)
        val tokenStream = CommonTokenStream(lexer)
        tokenStream.fill()
        return tokenStream.tokens
    }

    fun evaluate(macro: Macro, arguments: List<String>, location: SourceLocation): String {
        if (macro.parameters.size != arguments.size) {
            Messages.INCORRECT_MACRO_ARGUMENTS.on(location, macro.parameters.size, arguments.size)
            return ""
        }
        val builder = StringBuilder()
        macro.tokens.forEach {
            when (it.type) {
                MacroContentLexer.IDENTIFIER -> {
                    val index = macro.parameters.indexOf(it.text)
                    if (index != -1) {
                        builder.append(arguments[index])
                    } else {
                        builder.append(it.text)
                    }
                }
                MacroContentLexer.ESCAPE_DQ -> builder.append("\"")
                MacroContentLexer.ESCAPE_SLASH_DQ -> builder.append("\\\"")
                MacroContentLexer.TEXT -> builder.append(it.text)
                else -> {}
            }
        }
        return builder.toString()
    }

    private class LexerErrorListener(
        private val location: SourceLocation
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val message = RecognitionExceptionFormatter.format(e)
            Messages.MACRO_CONTENT_LEXER_ERROR.on(location, message)
        }
    }
}
