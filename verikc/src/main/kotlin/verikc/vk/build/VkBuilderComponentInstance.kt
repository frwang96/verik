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
import verikc.lang.LangSymbol.FUNCTION_WITH_COMPONENT
import verikc.lang.LangSymbol.PROPERTY_NULL
import verikc.lang.LangSymbol.TYPE_EVENT
import verikc.rs.ast.RsExpression
import verikc.rs.ast.RsExpressionFunction
import verikc.rs.ast.RsExpressionProperty
import verikc.rs.ast.RsProperty
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

        if (property.expression == null)
            throw LineException("property expression expected", property.line)
        val (connections, connectionIdentifiers) = getConnections(property.expression)

        val firstConnection = connections.firstOrNull()
        return if (firstConnection?.expression != null
            && firstConnection.expression.typeGenerified.typeSymbol == TYPE_EVENT
        ) {
            VkComponentInstance(
                VkProperty(
                    property.line,
                    property.identifier,
                    property.symbol,
                    property.mutabilityType,
                    property.getTypeGenerifiedNotNull()
                ),
                firstConnection.expression,
                connectionIdentifiers?.drop(1),
                connections.drop(1),
                null
            )
        } else {
            VkComponentInstance(
                VkProperty(
                    property.line,
                    property.identifier,
                    property.symbol,
                    property.mutabilityType,
                    property.getTypeGenerifiedNotNull()
                ),
                null,
                connectionIdentifiers,
                connections,
                null
            )
        }
    }

    private fun getConnections(expression: RsExpression): Pair<List<VkConnection>, List<String>?> {
        if (expression !is RsExpressionFunction)
            throw LineException("illegal component instantiation", expression.line)

        return if (expression.receiver != null) {
            if (expression.functionSymbol == FUNCTION_WITH_COMPONENT) {
                val connections = expression.args.map {
                    if (it is RsExpressionProperty && it.propertySymbol == PROPERTY_NULL) {
                        VkConnection(it.line, null, null, null, null)
                    } else {
                        val expressionPropertyIdentifier = if (it is RsExpressionProperty && it.receiver == null) {
                            it.identifier
                        } else null
                        VkConnection(it.line, VkExpression(it), expressionPropertyIdentifier, null, null)
                    }
                }
                return Pair(connections, expression.argIdentifiers)
            } else throw LineException("illegal component instantiation", expression.line)
        } else Pair(listOf(), null)
    }
}
