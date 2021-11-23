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

package io.verik.compiler.util

import io.verik.compiler.message.MessagePrinter
import io.verik.compiler.message.SourceLocation

class TestMessagePrinter : MessagePrinter() {

    override fun warning(message: String, location: SourceLocation) {
        throw TestWarningException(message)
    }

    override fun error(message: String, location: SourceLocation) {
        throw TestErrorException(message)
    }
}
