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

package io.verik.compiler.main

import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity

enum class MessageSeverity {
    EXCEPTION,
    ERROR,
    WARNING,
    INFO;

    companion object {

        operator fun invoke(severity: CompilerMessageSeverity): MessageSeverity {
            return when (severity) {
                CompilerMessageSeverity.EXCEPTION -> EXCEPTION
                CompilerMessageSeverity.ERROR -> ERROR
                CompilerMessageSeverity.STRONG_WARNING -> WARNING
                CompilerMessageSeverity.WARNING -> WARNING
                CompilerMessageSeverity.INFO -> INFO
                CompilerMessageSeverity.LOGGING -> INFO
                CompilerMessageSeverity.OUTPUT -> INFO
            }
        }
    }
}