/*
 * Copyright 2020 Francis Wang
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

package io.verik.core.vk

import io.verik.core.LineException

sealed class VkInstanceType {

    companion object {

        operator fun invoke(expression: VkExpressionCallable): VkInstanceType {
            val identifier = if (expression.target is VkExpressionIdentifier) expression.target.identifier
            else throw LineException("type declaration expected", expression)

            val expressions = expression.args
            val parameters = expressions.map {
                if (it is VkExpressionLiteral) it.value.toInt()
                else throw LineException("only integer literals are supported", it)
            }

            return when (identifier) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw LineException("type _bool does not take parameters", expression)
                }
                "_sint" -> {
                    if (parameters.size == 1) VkSintType(parameters[0])
                    else throw LineException("type _sint takes one parameter", expression)
                }
                "_uint" -> {
                    if (parameters.size == 1) VkUintType(parameters[0])
                    else throw LineException("type _uint takes one parameter", expression)
                }
                else -> {
                    if (parameters.isEmpty()) {
                        if (identifier.length <= 1) {
                            throw LineException("illegal identifier", expression.target)
                        }
                        if (identifier[0] != '_') {
                            throw LineException("identifier must begin with an underscore", expression.target)
                        }
                        VkNamedType(identifier)
                    }
                    else throw LineException("parameters to named types are not supported", expression)
                }
            }
        }
    }
}

object VkUnitType: VkInstanceType()

object VkBoolType: VkInstanceType()

data class VkSintType(val len: Int): VkInstanceType()

data class VkUintType(val len: Int): VkInstanceType()

data class VkNamedType(val identifier: String): VkInstanceType() {

    fun extract() = identifier.drop(1)
}
