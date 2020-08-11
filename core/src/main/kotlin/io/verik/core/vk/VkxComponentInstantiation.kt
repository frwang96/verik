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

import io.verik.core.main.LineException
import io.verik.core.kt.*
import io.verik.core.main.Symbol

data class VkxComponentInstantiation(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val ktType: Symbol?,
        val vkType: Symbol?,
        val connections: List<VkxConnection>
): VkxDeclaration {

    companion object {

        fun isComponentInstantiation(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationProperty && declaration.annotations.any { it == KtAnnotationProperty.COMP }
        }

        operator fun invoke(declaration: KtDeclaration): VkxComponentInstantiation {
            val declarationProperty = declaration.let {
                if (it is KtDeclarationProperty) it
                else throw LineException("property declaration expected", it)
            }
            if (declarationProperty.annotations.isEmpty()) {
                throw LineException("component annotation expected", declarationProperty)
            }
            if (declarationProperty.annotations.size > 1 || declarationProperty.annotations[0] != KtAnnotationProperty.COMP) {
                throw LineException("illegal component annotation", declarationProperty)
            }

            val connections = getConnections(declarationProperty.expression)

            return VkxComponentInstantiation(
                    declarationProperty.line,
                    declarationProperty.identifier,
                    declarationProperty.symbol,
                    null,
                    null,
                    connections
            )
        }

        private fun getConnections(expression: KtExpression): List<VkxConnection> {
            return when (expression) {
                is KtExpressionFunction -> {
                    if (expression.target == null) listOf()
                    else throw LineException("illegal component instantiation", expression)
                }
                is KtExpressionOperator -> {
                    if (expression.identifier == KtOperatorIdentifier.INFIX_WITH) {
                        expression
                                .blocks[0]
                                .statements
                                .map { VkxConnection(it) }
                    } else throw LineException("with expression expected", expression)
                }
                else -> throw LineException("illegal component instantiation", expression)
            }
        }
    }
}
