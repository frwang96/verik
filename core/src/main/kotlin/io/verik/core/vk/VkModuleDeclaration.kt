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

import io.verik.core.main.Line
import io.verik.core.main.LineException
import io.verik.core.sv.SvConnection
import io.verik.core.sv.SvModuleDeclaration

data class VkConnection(
        override val line: Int,
        val identifier: String,
        val expression: VkExpression
): Line {

    fun extract(): SvConnection {
        return SvConnection(line, identifier, expression.extractExpression())
    }
}

data class VkModuleDeclaration(
        override val line: Int,
        val moduleType: VkNamedType,
        val identifier: String,
        val connections: List<VkConnection>
): Line {

    fun extract(): SvModuleDeclaration {
        val svModuleType = moduleType.extract()
        val svConnections = connections.map { it.extract() }
        return SvModuleDeclaration(line, svModuleType, identifier, svConnections)
    }

    companion object {

        fun isModuleDeclaration(propertyDeclaration: VkPropertyDeclaration) =
                propertyDeclaration.annotations.any { it == VkPropertyAnnotation.COMP }

        operator fun invoke(propertyDeclaration: VkPropertyDeclaration): VkModuleDeclaration {
            if (propertyDeclaration.annotations.size != 1
                    || propertyDeclaration.annotations[0] != VkPropertyAnnotation.COMP) {
                throw LineException("illegal declaration annotations", propertyDeclaration)
            }

            return when(val expression = propertyDeclaration.expression) {
                is VkExpressionCallable -> {
                    val type = VkInstanceType(expression)
                    val moduleType = if (type is VkNamedType) type
                    else throw LineException("module type expected", propertyDeclaration)

                    VkModuleDeclaration(propertyDeclaration.line, moduleType, propertyDeclaration.identifier, listOf())
                }
                is VkExpressionOperator -> {
                    if (expression.type != VkOperatorType.WITH) {
                        throw LineException("module connection list expected", propertyDeclaration)
                    }
                    val type = expression.args[0].let {
                        if (it is VkExpressionCallable) VkInstanceType(it)
                        else throw LineException("module type expected", propertyDeclaration)
                    }
                    val moduleType = if (type is VkNamedType) type
                    else throw LineException("module type expected", propertyDeclaration)

                    val lambdaExpression = expression.args[1].let {
                        if (it is VkExpressionLambda) it
                        else throw LineException("module connections expected", expression)
                    }
                    val connections = lambdaExpression.statements.map { getConnection(it) }

                    VkModuleDeclaration(propertyDeclaration.line, moduleType, propertyDeclaration.identifier, connections)
                }
                else -> throw LineException("module declaration expected", propertyDeclaration)
            }
        }

        private fun getConnection(statement: VkStatement): VkConnection {
            return when (val expression = statement.expression) {
                is VkExpressionOperator -> {
                    if (expression.type == VkOperatorType.CON) {
                        val target = expression.args[0]
                        val identifier = if (target is VkExpressionNavigation
                                && target.target is VkExpressionIdentifier
                                && target.target.identifier == "it") {
                                target.identifier
                        } else throw LineException("module connection target expected", target)
                        VkConnection(expression.line, identifier, expression.args[1])
                    }
                    else throw LineException("module connection expected", statement)
                }
                is VkExpressionIdentifier -> VkConnection(expression.line, expression.identifier, expression)
                else -> throw LineException("module connection expected", statement)
            }
        }
    }
}