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

package io.verik.compiler.serialize

import io.verik.compiler.ast.common.Type
import io.verik.compiler.ast.descriptor.CardinalLiteralDescriptor
import io.verik.compiler.ast.element.VkDeclaration
import io.verik.compiler.ast.element.VkElement
import io.verik.compiler.core.CoreClass
import io.verik.compiler.main.messageCollector

object TypeSerializer {

    fun serialize(declaration: VkDeclaration): String? {
        val type = declaration.type
        return when (type.classifierDescriptor) {
            CoreClass.BOOLEAN -> "logic"
            CoreClass.UNIT -> "void"
            CoreClass.UBIT -> "logic ${serializeCardinalLittleEndian(type.arguments[0], declaration)}"
            else -> {
                messageCollector.error("Unable to serialize type: $type", declaration)
                null
            }
        }
    }

    private fun serializeCardinalLittleEndian(type: Type, element: VkElement): String {
        return if (type.classifierDescriptor is CardinalLiteralDescriptor) {
            val cardinal = type.classifierDescriptor.cardinal
            "[${cardinal - 1}:0]"
        } else {
            messageCollector.error("Could not deduce value of cardinal: $type", element)
            "[0:0]"
        }
    }
}