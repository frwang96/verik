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

import io.verik.importer.antlr.parse.SystemVerilogParser
import io.verik.importer.antlr.preprocess.PreprocessorParser
import io.verik.importer.parse.ParserToken
import org.antlr.v4.runtime.InputMismatchException
import org.antlr.v4.runtime.LexerNoViableAltException
import org.antlr.v4.runtime.NoViableAltException
import org.antlr.v4.runtime.RecognitionException

object RecognitionExceptionFormatter {

    fun format(recognitionException: RecognitionException?): String {
        return when (recognitionException) {
            null -> "Mismatched token"
            is LexerNoViableAltException -> "Unable to recognize token"
            is NoViableAltException -> "No matching rules"
            is InputMismatchException -> {
                val token = recognitionException.offendingToken
                if (token is ParserToken) {
                    "Mismatched token: ${SystemVerilogParser.VOCABULARY.getDisplayName(token.type)}"
                } else {
                    "Mismatched token: ${PreprocessorParser.VOCABULARY.getDisplayName(token.type)}"
                }
            }
            else -> "Unknown error"
        }
    }
}
