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
import io.verik.importer.main.ImporterContext
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.ParseTree
import java.nio.file.Path

object PreprocessParserStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        importerContext.inputFileContexts.forEach { (file, inputFileContext) ->
            inputFileContext.parseTree = parse(file, inputFileContext.content)
        }
    }

    private fun parse(file: Path, content: String): ParseTree {
        val charStream = CharStreams.fromString(content)
        val lexer = SystemVerilogPreprocessorLexer(charStream)
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
            throw IllegalArgumentException("${file.fileName}$line:$charPositionInLine: $msg")
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
            throw IllegalArgumentException("${file.fileName}$line:$charPositionInLine: $msg")
        }
    }
}
