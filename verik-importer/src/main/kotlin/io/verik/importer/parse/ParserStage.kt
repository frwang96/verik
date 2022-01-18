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

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.main.InputFileContext
import io.verik.importer.main.ProjectContext
import io.verik.importer.main.ProjectStage
import io.verik.importer.message.Messages
import io.verik.importer.message.RecognitionExceptionFormatter
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

object ParserStage : ProjectStage() {

    override fun process(projectContext: ProjectContext) {
        projectContext.inputFileContexts.forEach {
            processInputFileContext(it)
        }
    }

    private fun processInputFileContext(inputFileContext: InputFileContext) {
        val parserTokenSource = ParserTokenSource(
            inputFileContext.lexerCharStream,
            inputFileContext.lexerFragments
        )
        val parserTokenStream = CommonTokenStream(parserTokenSource)
        val parser = SystemVerilogParser(parserTokenStream)
        val parserErrorListener = ParserErrorListener(inputFileContext.lexerCharStream)
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)
        inputFileContext.parserTokenStream = parserTokenStream
        inputFileContext.parseTree = parser.sourceText()
    }

    private class ParserErrorListener(
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
            Messages.PARSER_ERROR.on(location, message)
        }
    }
}
