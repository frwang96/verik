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

import kotlin.reflect.full.declaredMemberProperties

object Messages {

    val INTERNAL_ERROR = FatalMessageTemplate1<String>(
        "Internal error: $0"
    )

// PREPROCESS //////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PREPROCESSOR_LEXER_ERROR = WarningMessageTemplate1<String>(
        "Preprocessor lexer error: $0"
    )

    val PREPROCESSOR_PARSER_ERROR = WarningMessageTemplate1<String>(
        "Preprocessor parser error: $0"
    )

    val UNMATCHED_ENDIF = WarningMessageTemplate0(
        "Unmatched endif directive"
    )

    val UNDEFINED_MACRO = WarningMessageTemplate1<String>(
        "Undefined macro: $0"
    )

    val INCORRECT_MACRO_ARGUMENTS = WarningMessageTemplate2<Int, Int>(
        "Incorrect number of macro arguments: Expected $0 actual $1"
    )

// LEX /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val LEXER_ERROR = WarningMessageTemplate1<String>(
        "Lexer error: $0"
    )

// FILTER //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val MISMATCHED_TOKEN = WarningMessageTemplate1<String>(
        "Mismatched token: $0"
    )

// PARSE ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PARSER_ERROR = WarningMessageTemplate1<String>(
        "Parser error: $0"
    )

// CAST ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val TYPE_CAST_ERROR = WarningMessageTemplate1<String>(
        "Unable to cast type: $0"
    )

    init {
        Messages::class.declaredMemberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
