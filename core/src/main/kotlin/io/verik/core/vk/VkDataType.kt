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

import io.verik.core.LinePosException

sealed class VkDataType {

    companion object {

        operator fun invoke(expression: VkCallableExpression): VkDataType {
            val identifier = if (expression.target is VkIdentifierExpression) expression.target.identifier
            else throw LinePosException("type declaration expected", expression.linePos)

            val expressions = expression.args
            val parameters = expressions.map {
                if (it is VkLiteralExpression) it.value.toInt()
                else throw LinePosException("only integer literals are supported", it.linePos)
            }

            return when (identifier) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw LinePosException("type _bool does not take parameters", expression.linePos)
                }
                "_sint" -> {
                    if (parameters.size == 1) VkSintType(parameters[0])
                    else throw LinePosException("type _sint takes one parameter", expression.linePos)
                }
                "_uint" -> {
                    if (parameters.size == 1) VkUintType(parameters[0])
                    else throw LinePosException("type _uint takes one parameter", expression.linePos)
                }
                else -> {
                    if (parameters.isEmpty()) {
                        if (identifier.length <= 1) {
                            throw LinePosException("illegal identifier", expression.target.linePos)
                        }
                        if (identifier[0] != '_') {
                            throw LinePosException("identifier must begin with an underscore", expression.target.linePos)
                        }
                        VkNamedType(identifier)
                    }
                    else throw LinePosException("parameters to named types are not supported", expression.linePos)
                }
            }
        }
    }
}

object VkUnitType: VkDataType()

object VkBoolType: VkDataType()

data class VkSintType(val len: Int): VkDataType()

data class VkUintType(val len: Int): VkDataType()

data class VkNamedType(val identifier: String): VkDataType() {

    fun extract() = identifier.drop(1)
}
