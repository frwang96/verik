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

    val INTERNAL_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "$0"
    )

// CONFIG //////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val DUPLICATED_FILE = MessageTemplate0(
        Severity.ERROR,
        "Imported file is duplicated"
    )

// PREPROCESS //////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PREPROCESSOR_LEXER_ERROR = MessageTemplate1<String>(
        Severity.WARNING,
        "Preprocessor lexer error: $0"
    )

    val PREPROCESSOR_PARSER_ERROR = MessageTemplate1<String>(
        Severity.WARNING,
        "Preprocessor parser error: $0"
    )

// PARSE ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val LEXER_ERROR = MessageTemplate1<String>(
        Severity.WARNING,
        "Lexer error: $0"
    )

    val PARSER_ERROR = MessageTemplate1<String>(
        Severity.WARNING,
        "Parser error: $0"
    )

    init {
        Messages::class.declaredMemberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
