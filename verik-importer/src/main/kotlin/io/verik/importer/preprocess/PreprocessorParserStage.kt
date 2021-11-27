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

import io.verik.importer.antlr.SystemVerilogPreprocessorLexer
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.common.ImporterStage
import io.verik.importer.common.RecognitionExceptionFormatter
import io.verik.importer.main.ImporterContext
import io.verik.importer.message.Messages
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.ParseTree
import java.nio.file.Path

object PreprocessorParserStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        importerContext.inputFileContexts.forEach { (file, inputFileContext) ->
            inputFileContext.parseTree = parse(file, inputFileContext.content)
        }
    }

    private fun parse(file: Path, content: String): ParseTree {
        val preprocessorCharStream = PreprocessorCharStream(file, content)
        val lexer = SystemVerilogPreprocessorLexer(preprocessorCharStream)
        val lexerErrorListener = LexerErrorListener(file)
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)

        val tokenStream = CommonTokenStream(lexer)
        val parser = SystemVerilogPreprocessorParser(tokenStream)
        val parserErrorListener = ParserErrorListener(file)
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)
        return parser.file()
    }

    private class LexerErrorListener(
        private val file: Path
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val location = SourceLocation(file, line, charPositionInLine)
            val message = RecognitionExceptionFormatter.format(e)
            Messages.PREPROCESSOR_LEXER_ERROR.on(location, message)
        }
    }

    private class ParserErrorListener(
        private val file: Path
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val location = SourceLocation(file, line, charPositionInLine)
            val message = RecognitionExceptionFormatter.format(e)
            Messages.PREPROCESSOR_PARSER_ERROR.on(location, message)
        }
    }
}