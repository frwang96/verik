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

import io.verik.core.FileLine
import io.verik.core.FileLineException
import io.verik.core.sv.SvConnection
import io.verik.core.sv.SvModuleDeclaration

data class VkConnection(
        val identifier: String,
        val expression: VkExpression,
        val fileLine: FileLine
) {

    fun extract(): SvConnection {
        return SvConnection(identifier, expression.extractExpression(), fileLine)
    }
}

data class VkModuleDeclaration(
        val moduleType: VkNamedType,
        val identifier: String,
        val connections: List<VkConnection>,
        val fileLine: FileLine
) {

    fun extract(): SvModuleDeclaration {
        val svModuleType = moduleType.extract()
        val svConnections = connections.map { it.extract() }
        return SvModuleDeclaration(svModuleType, identifier, svConnections, fileLine)
    }

    companion object {

        fun isModuleDeclaration(propertyDeclaration: VkPropertyDeclaration) =
                propertyDeclaration.annotations.any { it == VkPropertyAnnotation.COMP }

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkModuleDeclaration {
            if (propertyDeclaration.annotations.size != 1
                    || propertyDeclaration.annotations[0] != VkPropertyAnnotation.COMP) {
                throw FileLineException("illegal declaration annotations", propertyDeclaration.fileLine)
            }

            return when(val expression = propertyDeclaration.expression) {
                is VkCallableExpression -> {
                    val dataType = VkDataType(expression)
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw FileLineException("module type expected", propertyDeclaration.fileLine)

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, listOf(), propertyDeclaration.fileLine)
                }
                is VkOperatorExpression -> {
                    if (expression.type != VkOperatorType.WITH) {
                        throw FileLineException("module connection list expected", propertyDeclaration.fileLine)
                    }
                    val dataType = expression.args[0].let {
                        if (it is VkCallableExpression) VkDataType(it)
                        else throw FileLineException("module type expected", propertyDeclaration.fileLine)
                    }
                    val moduleType = if (dataType is VkNamedType) dataType
                    else throw FileLineException("module type expected", propertyDeclaration.fileLine)

                    val lambdaExpression = expression.args[1].let {
                        if (it is VkLambdaExpression) it
                        else throw FileLineException("module connections expected", expression.fileLine)
                    }
                    val connections = lambdaExpression.statements.map { getConnection(it) }

                    VkModuleDeclaration(moduleType, propertyDeclaration.identifier, connections, propertyDeclaration.fileLine)
                }
                else -> throw FileLineException("module declaration expected", propertyDeclaration.fileLine)
            }
        }

        private fun getConnection(statement: VkStatement): VkConnection {
            return when (val expression = statement.expression) {
                is VkOperatorExpression -> {
                    if (expression.type == VkOperatorType.CON) {
                        val target = expression.args[0]
                        val identifier = if (target is VkNavigationExpression
                                && target.target is VkIdentifierExpression
                                && target.target.identifier == "it") {
                                target.identifier
                        } else throw FileLineException("module connection target expected", target.fileLine)
                        VkConnection(identifier, expression.args[1], expression.fileLine)
                    }
                    else throw FileLineException("module connection expected", statement.fileLine)
                }
                is VkIdentifierExpression -> VkConnection(expression.identifier, expression, expression.fileLine)
                else -> throw FileLineException("module connection expected", statement.fileLine)
            }
        }
    }
}