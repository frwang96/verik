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

package verik.core.vk

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.*
import verik.core.lang.LangSymbol.OPERATOR_WITH

data class VkComponentInstance(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val connections: List<VkConnection>
): VkProperty {

    companion object {

        fun isComponentInstance(declaration: KtDeclaration): Boolean {
            return declaration is KtDeclarationPrimaryProperty
                    && declaration.annotations.any { it == KtAnnotationProperty.MAKE }
        }

        operator fun invoke(declaration: KtDeclaration): VkComponentInstance {
            val primaryProperty = declaration.let {
                if (it is KtDeclarationPrimaryProperty) it
                else throw LineException("primary property declaration expected", it)
            }

            if (primaryProperty.annotations.isEmpty()) {
                throw LineException("component annotation expected", primaryProperty)
            }
            if (primaryProperty.annotations.size > 1
                    || primaryProperty.annotations[0] != KtAnnotationProperty.MAKE) {
                throw LineException("illegal component annotation", primaryProperty)
            }

            val type = primaryProperty.type
                    ?: throw LineException("component instance has not been assigned a type", primaryProperty)

            val connections = getConnections(primaryProperty.expression)

            return VkComponentInstance(
                    primaryProperty.line,
                    primaryProperty.identifier,
                    primaryProperty.symbol,
                    type,
                    connections
            )
        }

        private fun getConnections(expression: KtExpression): List<VkConnection> {
            return when (expression) {
                is KtExpressionFunction -> {
                    if (expression.receiver == null) listOf()
                    else throw LineException("illegal component instantiation", expression)
                }
                is KtExpressionOperator -> {
                    if (expression.operator == OPERATOR_WITH) {
                        val receiver = expression.blocks[0].lambdaParameters[0].symbol
                        expression.blocks[0].statements.map { VkConnection(it, receiver) }
                    } else throw LineException("with expression expected", expression)
                }
                else -> throw LineException("illegal component instantiation", expression)
            }
        }
    }
}
