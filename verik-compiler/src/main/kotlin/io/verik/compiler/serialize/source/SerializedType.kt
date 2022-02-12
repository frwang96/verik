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

package io.verik.compiler.serialize.source

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.message.Messages

class SerializedType(
    val base: String,
    val variableDimension: String? = null,
    val isVirtual: Boolean = false
) {

    fun checkNoVariableDimension(element: EElement) {
        if (variableDimension != null) {
            Messages.INTERNAL_ERROR.on(element, "Unexpected variable dimension on type: $variableDimension")
        }
    }
}
