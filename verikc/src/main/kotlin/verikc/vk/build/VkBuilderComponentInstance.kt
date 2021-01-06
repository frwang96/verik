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
import verikc.lang.LangSymbol.OPERATOR_WITH
import verikc.rs.ast.*
import verikc.vk.ast.VkComponentInstance
import verikc.vk.ast.VkConnection

object VkBuilderComponentInstance {

    fun match(declaration: RsDeclaration): Boolean {
        return declaration is RsPrimaryProperty
                && declaration.annotations.any { it == AnnotationProperty.MAKE }
    }

    fun build(declaration: RsDeclaration): VkComponentInstance {
        val primaryProperty = declaration.let {
            if (it is RsPrimaryProperty) it
            else throw LineException("primary property declaration expected", it.line)
        }

        if (primaryProperty.annotations.isEmpty()) {
            throw LineException("component annotation expected", primaryProperty.line)
        }
        if (primaryProperty.annotations.size > 1 || primaryProperty.annotations[0] != AnnotationProperty.MAKE) {
            throw LineException("illegal component annotation", primaryProperty.line)
        }

        val typeSymbol = primaryProperty.typeSymbol
            ?: throw LineException("component instance has not been assigned a type", primaryProperty.line)

        val connections = getConnections(primaryProperty.expression)

        return VkComponentInstance(
            primaryProperty.line,
            primaryProperty.identifier,
            primaryProperty.symbol,
            typeSymbol,
            connections
        )
    }

    private fun getConnections(expression: RsExpression): List<VkConnection> {
        return when (expression) {
            is RsExpressionFunction -> {
                if (expression.receiver == null) listOf()
                else throw LineException("illegal component instantiation", expression.line)
            }
            is RsExpressionOperator -> {
                if (expression.operatorSymbol == OPERATOR_WITH) {
                    val receiver = expression.blocks[0].lambdaProperties[0].symbol
                    expression.blocks[0].statements.map { VkBuilderConnection.build(it, receiver) }
                } else throw LineException("with expression expected", expression.line)
            }
            else -> throw LineException("illegal component instantiation", expression.line)
        }
    }
}
