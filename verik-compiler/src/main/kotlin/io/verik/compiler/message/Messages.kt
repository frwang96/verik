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

import kotlin.reflect.full.memberProperties

object Messages {

    val KOTLIN_COMPILE_WARNING = WarningMessageTemplate("$1")
    val KOTLIN_COMPILE_ERROR = ErrorMessageTemplate("$1")

    val INTERNAL_ERROR = FatalMessageTemplate("$1")

    init {
        Messages::class.memberProperties.forEach {
            val messageTemplate = it.get(Messages)
            if (messageTemplate is MessageTemplate)
                messageTemplate.name = it.name
        }
    }
}
