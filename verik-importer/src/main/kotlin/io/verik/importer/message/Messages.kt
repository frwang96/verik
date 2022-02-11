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

import io.verik.importer.main.ProjectStage
import java.nio.file.Path
import kotlin.reflect.full.declaredMemberProperties

object Messages {

    val INTERNAL_ERROR = FatalMessageTemplate1<String>(
        "Internal error: $0"
    )

    val FILE_READ_ERROR = FatalMessageTemplate1<Path>(
        "Unable to read file: $0"
    )

    val FILE_WRITE_ERROR = FatalMessageTemplate1<Path>(
        "Unable to write file: $0"
    )

    val INCLUDED_FILE_NOT_FOUND = ErrorMessageTemplate1<Path>(
        "Included file not found: $0"
    )

    val NORMALIZATION_ERROR = FatalMessageTemplate2<ProjectStage, String>(
        "Normalization error at $0: $1"
    )

// PREPROCESS //////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PREPROCESSOR_LEXER_ERROR = ErrorMessageTemplate1<String>(
        "Preprocessor lexer error: $0"
    )

    val PREPROCESSOR_PARSER_ERROR = ErrorMessageTemplate1<String>(
        "Preprocessor parser error: $0"
    )

    val UNMATCHED_DIRECTIVE = ErrorMessageTemplate0(
        "Unmatched directive"
    )

    val UNDEFINED_MACRO = ErrorMessageTemplate1<String>(
        "Undefined macro: $0"
    )

    val INCORRECT_MACRO_ARGUMENTS = ErrorMessageTemplate2<Int, Int>(
        "Incorrect number of macro arguments: Expected $0 actual $1"
    )

// PARSE ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val LEXER_ERROR = ErrorMessageTemplate1<String>(
        "Lexer error: $0"
    )

    val PARSER_ERROR = ErrorMessageTemplate1<String>(
        "Parser error: $0"
    )

// CAST ////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    val UNABLE_TO_CAST = WarningMessageTemplate1<String>(
        "Unable to cast $0"
    )

// PRE TRANSFORM ///////////////////////////////////////////////////////////////////////////////////////////////////////

    val NAME_ALREADY_DEFINED = WarningMessageTemplate1<String>(
        "Name has already been defined: $0"
    )

    val UNRESOLVED_REFERENCE = WarningMessageTemplate1<String>(
        "Unable to resolve reference: $0"
    )

// INTERPRET ///////////////////////////////////////////////////////////////////////////////////////////////////////////

    val PARAMETERIZED_STATIC_PROPERTY = WarningMessageTemplate1<String>(
        "Parameterized static property is not supported: $0"
    )

    init {
        Messages::class.declaredMemberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
