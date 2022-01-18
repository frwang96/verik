/*
 * Copyright (c) 2022 Francis Wang
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

import io.verik.importer.antlr.SystemVerilogLexer
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages
import io.verik.importer.message.RecognitionExceptionFormatter
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.Token

object LexerStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.inputFileContexts.forEach {
            processInputFileContext(it)
        }
    }

    private fun processInputFileContext(inputFileContext: InputFileContext) {
        val lexerCharStream = LexerCharStream(
            inputFileContext.preprocessorFragments,
            inputFileContext.textFile
        )
        val lexer = SystemVerilogLexer(lexerCharStream)
        val lexerErrorListener = LexerErrorListener(lexerCharStream)
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)

        val tokenStream = CommonTokenStream(lexer)
        val lexerFragments = ArrayList<LexerFragment>()
        tokenStream.fill()
        tokenStream.tokens.forEach {
            if (it.channel == Token.DEFAULT_CHANNEL) {
                val location = lexerCharStream.getLocation(it.line, it.charPositionInLine)
                val lexerFragment = LexerFragment(
                    location,
                    it.line,
                    it.charPositionInLine,
                    it.type,
                    it.text
                )
                lexerFragments.add(lexerFragment)
            }
        }
        inputFileContext.lexerCharStream = lexerCharStream
        inputFileContext.lexerFragments = lexerFragments
    }

    private class LexerErrorListener(
        private val lexerCharStream: LexerCharStream
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val location = lexerCharStream.getLocation(line, charPositionInLine)
            val message = RecognitionExceptionFormatter.format(e)
            Messages.LEXER_ERROR.on(location, message)
        }
    }
}
