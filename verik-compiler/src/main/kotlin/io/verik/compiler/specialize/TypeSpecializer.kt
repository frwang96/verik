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
import io.verik.compiler.ast.element.kt.EKtClass
import io.verik.compiler.ast.property.Type
import io.verik.compiler.core.common.Cardinal
import io.verik.compiler.core.common.Core
import io.verik.compiler.core.common.CoreCardinalFunctionDeclaration
import io.verik.compiler.message.Messages

object TypeSpecializer {

    fun specialize(
        type: Type,
        specializeContext: SpecializeContext,
        element: EElement,
        forwardReferences: Boolean
    ): Type {
        val arguments = type.arguments.map { specialize(it, specializeContext, element, forwardReferences) }
        return when (val reference = type.reference) {
            is CoreCardinalFunctionDeclaration -> {
                val argumentValues = arguments.map { it.asCardinalValue(element) }
                val value = specializeCardinalFunction(reference, argumentValues, element)
                Cardinal.of(value).toType()
            }
            is EKtClass -> {
                if (forwardReferences) {
                    val argumentsNotForwarded = type.arguments
                        .map { specialize(it, specializeContext, element, false) }
                    val typeParameterContext = TypeParameterContext
                        .getFromTypeArguments(argumentsNotForwarded, reference, specializeContext, element)
                    val forwardedReference = specializeContext[reference, typeParameterContext, element]
                    forwardedReference.toType()
                } else {
                    reference.toType(arguments)
                }
            }
            is ETypeParameter -> {
                val typeParameterType = specializeContext.typeParameterContext.specialize(reference, element)
                specialize(typeParameterType, specializeContext, element, forwardReferences)
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
            Core.Vk.T_TRUE ->
                1
            Core.Vk.T_FALSE ->
                0
            Core.Vk.T_NOT -> {
                if (argumentValues[0] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                if (argumentValues[0] != 0) 0 else 1
            }
            Core.Vk.T_AND -> {
                if (argumentValues[0] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                if (argumentValues[1] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[1]).toType())
                if ((argumentValues[0] != 0) && (argumentValues[1] != 0)) 1 else 0
            }
            Core.Vk.T_OR -> {
                if (argumentValues[0] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                if (argumentValues[1] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[1]).toType())
                if ((argumentValues[0] != 0) || (argumentValues[1] != 0)) 1 else 0
            }
            Core.Vk.T_IF -> {
                if (argumentValues[0] !in 0..1)
                    Messages.CARDINAL_NOT_BOOLEAN.on(element, Cardinal.of(argumentValues[0]).toType())
                if (argumentValues[0] != 0) {
                    argumentValues[1]
                } else argumentValues[2]
            }
            Core.Vk.T_ADD ->
                argumentValues[0] + argumentValues[1]
            Core.Vk.T_SUB ->
                argumentValues[0] - argumentValues[1]
            Core.Vk.T_MUL ->
                argumentValues[0] * argumentValues[1]
            Core.Vk.T_DIV ->
                argumentValues[0] / argumentValues[1]
            Core.Vk.T_MAX ->
                Integer.max(argumentValues[0], argumentValues[1])
            Core.Vk.T_MIN ->
                Integer.min(argumentValues[0], argumentValues[1])
            Core.Vk.T_OF ->
                argumentValues[0]
            Core.Vk.T_INC ->
                argumentValues[0] + 1
            Core.Vk.T_DEC ->
                argumentValues[0] - 1
            Core.Vk.T_LOG ->
                if (argumentValues[0] <= 0) 0 else (32 - (argumentValues[0] - 1).countLeadingZeroBits())
            Core.Vk.T_WIDTH ->
                if (argumentValues[0] < 0) 0 else (32 - argumentValues[0].countLeadingZeroBits())
            Core.Vk.T_EXP -> {
                if (argumentValues[0] >= 31)
                    Messages.CARDINAL_OUT_OF_RANGE.on(element)
                1 shl argumentValues[0]
            }
            else ->
                Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: ${reference.name}")
        }
    }
}
