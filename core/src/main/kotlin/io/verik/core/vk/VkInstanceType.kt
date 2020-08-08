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

import io.verik.core.FileLineException

sealed class VkInstanceType {

    companion object {

        operator fun invoke(expression: VkExpressionCallable): VkInstanceType {
            val identifier = if (expression.target is VkExpressionIdentifier) expression.target.identifier
            else throw FileLineException("type declaration expected", expression.fileLine)

            val expressions = expression.args
            val parameters = expressions.map {
                if (it is VkExpressionLiteral) it.value.toInt()
                else throw FileLineException("only integer literals are supported", it.fileLine)
            }

            return when (identifier) {
                "_bool" -> {
                    if (parameters.isEmpty()) VkBoolType
                    else throw FileLineException("type _bool does not take parameters", expression.fileLine)
                }
                "_sint" -> {
                    if (parameters.size == 1) VkSintType(parameters[0])
                    else throw FileLineException("type _sint takes one parameter", expression.fileLine)
                }
                "_uint" -> {
                    if (parameters.size == 1) VkUintType(parameters[0])
                    else throw FileLineException("type _uint takes one parameter", expression.fileLine)
                }
                else -> {
                    if (parameters.isEmpty()) {
                        if (identifier.length <= 1) {
                            throw FileLineException("illegal identifier", expression.target.fileLine)
                        }
                        if (identifier[0] != '_') {
                            throw FileLineException("identifier must begin with an underscore", expression.target.fileLine)
                        }
                        VkNamedType(identifier)
                    }
                    else throw FileLineException("parameters to named types are not supported", expression.fileLine)
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
