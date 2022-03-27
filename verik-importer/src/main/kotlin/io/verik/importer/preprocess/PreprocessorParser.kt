/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.preprocess

import io.verik.importer.antlr.SystemVerilogPreprocessorLexer
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.common.TextFile
import io.verik.importer.message.Messages
import io.verik.importer.message.RecognitionExceptionFormatter
import io.verik.importer.message.SourceLocation
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.ParseTree

object PreprocessorParser {

    fun parse(textFile: TextFile): ParseTree {
        return parse(textFile.content, SourceLocation(textFile.path, 0, 0), true)
    }

    fun parse(content: String, location: SourceLocation): ParseTree {
        return parse(content, location, false)
    }

    private fun parse(content: String, location: SourceLocation, isOriginal: Boolean): ParseTree {
        val preprocessorCharStream = PreprocessorCharStream(location, isOriginal, content)
        val lexer = SystemVerilogPreprocessorLexer(preprocessorCharStream)
        val lexerErrorListener = LexerErrorListener(location, isOriginal)
        lexer.removeErrorListeners()
        lexer.addErrorListener(lexerErrorListener)

        val tokenStream = CommonTokenStream(lexer)
        val parser = SystemVerilogPreprocessorParser(tokenStream)
        val parserErrorListener = ParserErrorListener(location, isOriginal)
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)
        return parser.file()
    }

    private class LexerErrorListener(
        private val location: SourceLocation,
        private val isOriginal: Boolean
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val location = if (isOriginal) {
                SourceLocation(location.path, line, charPositionInLine)
            } else location
            val message = RecognitionExceptionFormatter.format(e)
            Messages.PREPROCESSOR_LEXER_ERROR.on(location, message)
        }
    }

    private class ParserErrorListener(
        private val location: SourceLocation,
        private val isOriginal: Boolean
    ) : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?
        ) {
            val location = if (isOriginal) {
                SourceLocation(location.path, line, charPositionInLine)
            } else location
            val message = RecognitionExceptionFormatter.format(e)
            Messages.PREPROCESSOR_PARSER_ERROR.on(location, message)
        }
    }
}
