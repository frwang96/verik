/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.vkx.builder

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.gex.ast.*
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.vkx.ast.VkxComponentInstance
import verikc.vkx.ast.VkxConnection

object VkxBuilderComponentInstance {

    fun match(declaration: GexDeclaration): Boolean {
        return declaration is GexProperty && declaration.annotations.any { it == AnnotationProperty.MAKE }
    }

    fun build(property: GexProperty): VkxComponentInstance {
        if (property.annotations.isEmpty()) {
            throw LineException("component annotation expected", property.line)
        }
        if (property.annotations.size > 1 || property.annotations[0] != AnnotationProperty.MAKE) {
            throw LineException("illegal component annotation", property.line)
        }

        if (property.expression == null)
            throw LineException("property expression expected", property.line)
        val connections = getConnections(property.expression)

        return VkxComponentInstance(
            property.line,
            property.identifier,
            property.symbol,
            property.typeSymbol,
            connections
        )
    }

    private fun getConnections(expression: GexExpression): List<VkxConnection> {
        return when (expression) {
            is GexExpressionFunction -> {
                if (expression.receiver == null) listOf()
                else throw LineException("illegal component instantiation", expression.line)
            }
            is GexExpressionOperator -> {
                if (expression.operatorSymbol == OPERATOR_WITH) {
                    val receiver = expression.blocks[0].lambdaProperties[0].symbol
                    expression.blocks[0].statements.map { VkxBuilderConnection.build(it, receiver) }
                } else throw LineException("with expression expected", expression.line)
            }
            else -> throw LineException("illegal component instantiation", expression.line)
        }
    }
}
