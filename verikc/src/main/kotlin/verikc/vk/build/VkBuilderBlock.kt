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

package verikc.vk.build

import verikc.ge.ast.GeBlock
import verikc.ge.ast.GeStatementDeclaration
import verikc.ge.ast.GeStatementExpression
import verikc.lang.LangSymbol
import verikc.vk.ast.*

object VkBuilderBlock {

    fun build(block: GeBlock): VkBlock {
        val properties = ArrayList<VkProperty>()
        val expressions = ArrayList<VkExpression>()
        block.statements.forEach {
            when (it) {
                is GeStatementDeclaration -> {
                    val property = VkProperty(it.property)
                    properties.add(property)
                    if (it.property.expression != null) {
                        val expression = VkExpressionFunction(
                            it.line,
                            LangSymbol.TYPE_UNIT.toTypeGenerifiedInstance(),
                            LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE,
                            VkExpressionProperty(it.line, property.typeGenerified, property.symbol, null),
                            arrayListOf(VkExpression(it.property.expression))
                        )
                        expressions.add(expression)
                    }
                }
                is GeStatementExpression -> {
                    expressions.add(VkExpression(it.expression))
                }
            }
        }
        return VkBlock(
            block.line,
            properties,
            expressions
        )
    }
}