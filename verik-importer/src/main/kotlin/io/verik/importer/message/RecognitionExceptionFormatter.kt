/*
 * SPDX-License-Identifier: Apache-2.0
 */

package io.verik.importer.message

import io.verik.importer.antlr.SystemVerilogParser
import io.verik.importer.antlr.SystemVerilogPreprocessorParser
import io.verik.importer.parse.ParserCharStream
import io.verik.importer.preprocess.PreprocessorCharStream
import org.antlr.v4.runtime.InputMismatchException
import org.antlr.v4.runtime.LexerNoViableAltException
import org.antlr.v4.runtime.NoViableAltException
import org.antlr.v4.runtime.RecognitionException

object RecognitionExceptionFormatter {

    fun format(recognitionException: RecognitionException?): String {
        return when (recognitionException) {
            null -> "No matching rules"
            is LexerNoViableAltException -> "Unable to recognize token"
            is NoViableAltException -> "No matching rules"
            is InputMismatchException -> {
                val token = recognitionException.offendingToken
                when (token.inputStream) {
                    is ParserCharStream ->
                        "Mismatched token: ${SystemVerilogParser.VOCABULARY.getDisplayName(token.type)}"
                    is PreprocessorCharStream ->
                        "Mismatched token: ${SystemVerilogPreprocessorParser.VOCABULARY.getDisplayName(token.type)}"
                    else -> "Unknown token type"
                }
            }
            else -> "Unknown error"
        }
    }
}
