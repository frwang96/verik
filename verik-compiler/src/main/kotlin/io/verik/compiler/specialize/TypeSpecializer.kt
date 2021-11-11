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
import io.verik.compiler.ast.element.common.ETypeParameter
import io.verik.compiler.ast.element.kt.EKtBasicClass
import io.verik.compiler.ast.property.Type
import io.verik.compiler.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.message.Messages

object TypeSpecializer {

    fun specialize(
        type: Type,
        specializerContext: SpecializerContext,
        element: EElement,
        forwardReferences: Boolean
    ): Type {
        val arguments = type.arguments.map { specialize(it, specializerContext, element, forwardReferences) }
        return when (val reference = type.reference) {
            is CoreCardinalFunctionDeclaration -> {
                val argumentValues = arguments.map { it.asCardinalValue(element) }
                val value = specializeCardinalFunction(reference, argumentValues, element)
                Cardinal.of(value).toType()
            }
            is EKtBasicClass -> {
                if (forwardReferences) {
                    val argumentsNotForwarded = type.arguments
                        .map { specialize(it, specializerContext, element, false) }
                    val typeParameterContext = TypeParameterContext
                        .getFromTypeArguments(argumentsNotForwarded, reference, element)
                    val forwardedReference = specializerContext[reference, typeParameterContext, element]
                    forwardedReference.toType()
                } else {
                    reference.toType(arguments)
                }
            }
            is ETypeParameter -> {
                val typeParameterType = specializerContext.typeParameterContext.specialize(reference, element)
                specialize(typeParameterType, specializerContext, element, forwardReferences)
            }
            else -> {
                type.reference.toType(arguments)
            }
        }
    }

    private fun specializeCardinalFunction(
        reference: CoreCardinalFunctionDeclaration,
        argumentValues: List<Int>,
        element: EElement
    ): Int {
        return when (reference) {
            Core.Vk.N_ADD ->
                argumentValues[0] + argumentValues[1]
            Core.Vk.N_SUB ->
                argumentValues[0] - argumentValues[1]
            Core.Vk.N_MUL ->
                argumentValues[0] * argumentValues[1]
            Core.Vk.N_DIV ->
                argumentValues[0] / argumentValues[1]
            Core.Vk.N_MAX ->
                Integer.max(argumentValues[0], argumentValues[1])
            Core.Vk.N_MIN ->
                Integer.min(argumentValues[0], argumentValues[1])
            Core.Vk.N_OF ->
                argumentValues[0]
            Core.Vk.N_INC ->
                argumentValues[0] + 1
            Core.Vk.N_DEC ->
                argumentValues[0] - 1
            Core.Vk.N_LOG ->
                if (argumentValues[0] <= 0) 0 else (32 - (argumentValues[0] - 1).countLeadingZeroBits())
            Core.Vk.N_WIDTH ->
                if (argumentValues[0] < 0) 0 else (32 - argumentValues[0].countLeadingZeroBits())
            Core.Vk.N_EXP -> {
                if (argumentValues[0] >= 31)
                    Messages.CARDINAL_OUT_OF_RANGE.on(element)
                1 shl argumentValues[0]
            }
            else -> {
                Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: $reference")
                1
            }
        }
    }
}
