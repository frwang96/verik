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

package verikc.vk.build

import verikc.base.ast.AnnotationProperty
import verikc.base.ast.LineException
import verikc.lang.LangSymbol
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.rs.ast.*
import verikc.vk.ast.VkComponentInstance
import verikc.vk.ast.VkConnection
import verikc.vk.ast.VkExpression
import verikc.vk.ast.VkProperty

object VkBuilderComponentInstance {

    fun match(property: RsProperty): Boolean {
        return property.annotations.any { it == AnnotationProperty.MAKE }
    }

    fun build(property: RsProperty): VkComponentInstance {
        if (property.annotations.isEmpty()) {
            throw LineException("component annotation expected", property.line)
        }
        if (property.annotations.size > 1 || property.annotations[0] != AnnotationProperty.MAKE) {
            throw LineException("illegal component annotation", property.line)
        }

        val (eventExpression, connections) = getEventExpressionAndConnections(property.getExpressionNotNull())

        return VkComponentInstance(
            VkProperty(
                property.line,
                property.identifier,
                property.symbol,
                property.mutabilityType,
                property.getTypeGenerifiedNotNull()
            ),
            eventExpression,
            connections,
            null
        )
    }

    private fun getEventExpressionAndConnections(expression: RsExpression): Pair<VkExpression?, List<VkConnection>> {
        return when (expression) {
            is RsExpressionFunction -> {
                if (expression.receiver == null) Pair(null, listOf())
                else throw LineException("illegal component instantiation", expression.line)
            }
            is RsExpressionOperator -> {
                if (expression.operatorSymbol == OPERATOR_WITH) {
                    getEventExpressionAndConnections(expression.blocks[0])
                } else throw LineException("with expression expected", expression.line)
            }
            else -> throw LineException("illegal component instantiation", expression.line)
        }
    }

    private fun getEventExpressionAndConnections(block: RsBlock): Pair<VkExpression?, List<VkConnection>> {
        val receiver = block.lambdaProperties[0].symbol
        val isOnExpression = { it: RsStatement ->
            it is RsStatementExpression
                    && it.expression is RsExpressionOperator
                    && it.expression.operatorSymbol == LangSymbol.OPERATOR_ON
        }
        return if (block.statements.any { isOnExpression(it) }) {
            if (block.statements.size != 1)
                throw LineException("illegal use of on expression", block.line)
            val onExpression = (block.statements[0] as RsStatementExpression).expression as RsExpressionOperator
            if (onExpression.args.size != 1)
                throw LineException("single event expression expected", onExpression.line)
            val eventExpression = VkExpression(onExpression.args[0])
            Pair(eventExpression, onExpression.blocks[0].statements.map { VkBuilderConnection.build(it, receiver) })
        } else {
            Pair(null, block.statements.map { VkBuilderConnection.build(it, receiver) })
        }
    }
}
