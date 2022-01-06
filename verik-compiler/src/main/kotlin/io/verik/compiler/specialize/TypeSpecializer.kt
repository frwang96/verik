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
import io.verik.compiler.core.common.CoreOptionalFunctionDeclaration
import io.verik.compiler.core.common.Optional
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
                val value = specializeCardinalFunction(reference, arguments, element)
                Cardinal.of(value).toType()
            }
            is CoreOptionalFunctionDeclaration -> {
                val value = specializeOptionalFunction(reference, arguments, element)
                Optional.of(value).toType()
            }
            is EKtClass -> {
                if (forwardReferences) {
                    val argumentsNotForwarded = type.arguments
                        .map { specialize(it, specializerContext, element, false) }
                    val typeParameterContext = TypeParameterContext
                        .getFromTypeArguments(argumentsNotForwarded, reference, specializerContext, element)
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
        arguments: List<Type>,
        element: EElement
    ): Int {
        return when (reference) {
            Core.Vk.T_ADD ->
                arguments[0].asCardinalValue(element) + arguments[1].asCardinalValue(element)
            Core.Vk.T_SUB ->
                arguments[0].asCardinalValue(element) - arguments[1].asCardinalValue(element)
            Core.Vk.T_MUL ->
                arguments[0].asCardinalValue(element) * arguments[1].asCardinalValue(element)
            Core.Vk.T_DIV ->
                arguments[0].asCardinalValue(element) / arguments[1].asCardinalValue(element)
            Core.Vk.T_MAX ->
                Integer.max(arguments[0].asCardinalValue(element), arguments[1].asCardinalValue(element))
            Core.Vk.T_MIN ->
                Integer.min(arguments[0].asCardinalValue(element), arguments[1].asCardinalValue(element))
            Core.Vk.T_OF ->
                arguments[0].asCardinalValue(element)
            Core.Vk.T_INC ->
                arguments[0].asCardinalValue(element) + 1
            Core.Vk.T_DEC ->
                arguments[0].asCardinalValue(element) - 1
            Core.Vk.T_LOG -> {
                val value = arguments[0].asCardinalValue(element)
                if (value <= 0) 0 else (32 - (value - 1).countLeadingZeroBits())
            }
            Core.Vk.T_WIDTH -> {
                val value = arguments[0].asCardinalValue(element)
                if (value < 0) 0 else (32 - value.countLeadingZeroBits())
            }
            Core.Vk.T_EXP -> {
                val value = arguments[0].asCardinalValue(element)
                if (value >= 31)
                    Messages.CARDINAL_OUT_OF_RANGE.on(element)
                1 shl value
            }
            Core.Vk.T_IF -> {
                if (arguments[0].asOptionalValue(element)) {
                    arguments[1].asCardinalValue(element)
                } else arguments[2].asCardinalValue(element)
            }
            else ->
                Messages.INTERNAL_ERROR.on(element, "Unrecognized cardinal function: ${reference.name}")
        }
    }

    private fun specializeOptionalFunction(
        reference: CoreOptionalFunctionDeclaration,
        arguments: List<Type>,
        element: EElement
    ): Boolean {
        return when (reference) {
            Core.Vk.T_NOT ->
                !arguments[0].asOptionalValue(element)
            else ->
                Messages.INTERNAL_ERROR.on(element, "Unrecognized optional function: ${reference.name}")
        }
    }
}
