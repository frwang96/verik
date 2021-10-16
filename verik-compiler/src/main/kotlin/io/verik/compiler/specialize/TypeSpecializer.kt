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

package io.verik.compiler.specialize

import io.verik.compiler.ast.element.common.EElement
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.message.Messages

object TypeSpecializer {

    // TODO specialize with type parameter context
    fun specialize(type: Type, element: EElement) {
        type.arguments.forEach { specialize(it, element) }
        val reference = type.reference
        if (reference is CoreCardinalFunctionDeclaration) {
            val arguments = type.arguments.map { it.asCardinalValue(element) }
            val value = specializeCardinalFunction(reference, arguments, element)
            type.reference = Core.Vk.cardinalOf(value)
            type.arguments = arrayListOf()
        }
    }

    private fun specializeCardinalFunction(
        reference: CoreCardinalFunctionDeclaration,
        arguments: List<Int>,
        element: EElement
    ): Int {
        return when (reference) {
            Core.Vk.N_ADD ->
                arguments[0] + arguments[1]
            Core.Vk.N_SUB ->
                arguments[0] - arguments[1]
            Core.Vk.N_MUL ->
                arguments[0] * arguments[1]
            Core.Vk.N_MAX ->
                Integer.max(arguments[0], arguments[1])
            Core.Vk.N_MIN ->
                Integer.min(arguments[0], arguments[1])
            Core.Vk.N_ID ->
                arguments[0]
            Core.Vk.N_INC ->
                arguments[0] + 1
            Core.Vk.N_DEC ->
                arguments[0] - 1
            Core.Vk.N_LOG ->
                if (arguments[0] <= 0) 0 else (32 - (arguments[0] - 1).countLeadingZeroBits())
            Core.Vk.N_WIDTH ->
                if (arguments[0] < 0) 0 else (32 - arguments[0].countLeadingZeroBits())
            Core.Vk.N_EXP -> {
                if (arguments[0] >= 31)
                    Messages.CARDINAL_OUT_OF_RANGE.on(element)
                1 shl arguments[0]
            }
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: $reference")
                1
            }
        }
    }
}
