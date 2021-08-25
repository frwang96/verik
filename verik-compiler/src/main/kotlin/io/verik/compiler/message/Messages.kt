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

package io.verik.compiler.message

import org.jetbrains.kotlin.name.FqName
import kotlin.reflect.full.memberProperties

object Messages {

    val INTERNAL_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "$0"
    )

    val KOTLIN_COMPILE_WARNING = MessageTemplate1<String>(
        Severity.WARNING,
        "$0"
    )

    val KOTLIN_COMPILE_ERROR = MessageTemplate1<String>(
        Severity.ERROR,
        "$0"
    )

    val ANNOTATION_NOT_RECOGNIZED = MessageTemplate1<FqName>(
        Severity.WARNING,
        "Annotation not recognized: $0"
    )

    val KEYWORD_CONFLICT_SYSTEM_VERILOG = MessageTemplate1<String>(
        Severity.ERROR,
        "Conflict with SystemVerilog reserved keyword: $0"
    )

    val BIT_ZERO_WIDTH = MessageTemplate0(
        Severity.ERROR,
        "Bit type cannot have zero width"
    )

    val BIT_CONSTANT_TRUNCATION = MessageTemplate2<Int, Int>(
        Severity.WARNING,
        "Converting constant $0 to width $1 results in truncation"
    )

    init {
        Messages::class.memberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is AbstractMessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
