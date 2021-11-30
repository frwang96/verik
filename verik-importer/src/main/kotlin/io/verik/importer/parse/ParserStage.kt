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
import io.verik.importer.common.ImporterStage
import io.verik.importer.common.RecognitionExceptionFormatter
import io.verik.importer.lex.LexerCharStream
import io.verik.importer.main.ImporterContext
import io.verik.importer.message.Messages
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer

object ParserStage : ImporterStage() {

    override fun process(importerContext: ImporterContext) {
        val tokenSource = ParserTokenSource(importerContext.lexerFragments)
        val tokenStream = CommonTokenStream(tokenSource)
        val parser = SystemVerilogParser(tokenStream)
        val parserErrorListener = ParserErrorListener(importerContext.lexerCharStream)
        parser.removeErrorListeners()
        parser.addErrorListener(parserErrorListener)
        importerContext.parseTree = parser.compilationUnit()
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
