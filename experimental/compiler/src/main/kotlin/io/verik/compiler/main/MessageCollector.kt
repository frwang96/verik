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

import io.verik.compiler.ast.VkElement

open class MessageCollector {

    var hasErrors = false

    open fun error(message: String, location: MessageLocation? = null) {
        hasErrors = true
    }

    open fun warning(message: String, location: MessageLocation? = null) {}

    open fun info(message: String, location: MessageLocation? = null) {}

    fun error(message: String, element: VkElement?) {
        error(message, element?.location)
    }
}