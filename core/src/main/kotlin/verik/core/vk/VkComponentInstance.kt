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

import verik.core.kt.KtAnnotationProperty
import verik.core.kt.KtOperatorIdentifier
import verik.core.ktx.*
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

data class VkComponentInstance(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        override val type: Symbol,
        val connections: List<VkConnection>
): VkProperty {

    companion object {

        fun isComponentInstance(declaration: KtxDeclaration): Boolean {
            return declaration is KtxDeclarationBaseProperty
                    && declaration.annotations.any { it == KtAnnotationProperty.COMP }
        }

        operator fun invoke(declaration: KtxDeclaration): VkComponentInstance {
            val baseProperty = declaration.let {
                if (it is KtxDeclarationBaseProperty) it
                else throw LineException("base property declaration expected", it)
            }
            if (baseProperty.annotations.isEmpty()) {
                throw LineException("component annotation expected", baseProperty)
            }
            if (baseProperty.annotations.size > 1
                    || baseProperty.annotations[0] != KtAnnotationProperty.COMP) {
                throw LineException("illegal component annotation", baseProperty)
            }

            val connections = getConnections(baseProperty.expression)

            return VkComponentInstance(
                    baseProperty.line,
                    baseProperty.identifier,
                    baseProperty.symbol,
                    baseProperty.type,
                    connections
            )
        }

        private fun getConnections(expression: KtxExpression): List<VkConnection> {
            return when (expression) {
                is KtxExpressionFunction -> {
                    if (expression.target == null) listOf()
                    else throw LineException("illegal component instantiation", expression)
                }
                is KtxExpressionOperator -> {
                    if (expression.identifier == KtOperatorIdentifier.INFIX_WITH) {
                        expression
                                .blocks[0]
                                .statements
                                .map { VkConnection(it) }
                    } else throw LineException("with expression expected", expression)
                }
                else -> throw LineException("illegal component instantiation", expression)
            }
        }
    }
}
