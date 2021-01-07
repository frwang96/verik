/*
 * Copyright (c) 2021 Francis Wang
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

import verikc.gex.ast.GexBlock
import verikc.gex.ast.GexStatementDeclaration
import verikc.gex.ast.GexStatementExpression
import verikc.lang.LangSymbol
import verikc.vkx.ast.*

object VkxBuilderBlock {

    fun build(block: GexBlock): VkxBlock {
        val properties = ArrayList<VkxProperty>()
        val expressions = ArrayList<VkxExpression>()
        block.statements.forEach {
            when (it) {
                is GexStatementDeclaration -> {
                    val property = VkxProperty(it.property)
                    properties.add(property)
                    if (it.property.expression != null) {
                        val expression = VkxExpressionFunction(
                            it.line,
                            LangSymbol.TYPE_UNIT.toTypeGenerifiedInstance(),
                            LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE,
                            VkxExpressionProperty(it.line, property.typeGenerified, property.symbol, null),
                            arrayListOf(VkxExpression(it.property.expression))
                        )
                        expressions.add(expression)
                    }
                }
                is GexStatementExpression -> {
                    expressions.add(VkxExpression(it.expression))
                }
            }
        }
        return VkxBlock(
            block.line,
            properties,
            expressions
        )
    }
}