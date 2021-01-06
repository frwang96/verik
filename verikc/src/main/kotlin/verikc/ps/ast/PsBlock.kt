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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.ge.ast.GeBlock
import verikc.ge.ast.GeStatementDeclaration
import verikc.ge.ast.GeStatementExpression
import verikc.lang.LangSymbol.FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE
import verikc.lang.LangSymbol.TYPE_UNIT

data class PsBlock(
    val line: Line,
    val primaryProperties: ArrayList<PsPrimaryProperty>,
    val expressions: ArrayList<PsExpression>
) {

    companion object {

        operator fun invoke(block: GeBlock): PsBlock {
            val primaryProperties = ArrayList<PsPrimaryProperty>()
            val expressions = ArrayList<PsExpression>()
            block.statements.forEach {
                when (it) {
                    is GeStatementDeclaration -> {
                        val primaryProperty = PsPrimaryProperty(it.primaryProperty)
                        val expression = PsExpression(it.primaryProperty.expression)
                        primaryProperties.add(primaryProperty)
                        expressions.add(
                            PsExpressionFunction(
                                it.line,
                                TYPE_UNIT.toTypeReifiedInstance(),
                                FUNCTION_NATIVE_ASSIGN_INSTANCE_INSTANCE,
                                PsExpressionProperty(
                                    it.line,
                                    primaryProperty.typeReified,
                                    primaryProperty.symbol,
                                    null
                                ),
                                arrayListOf(expression)
                            )
                        )
                    }
                    is GeStatementExpression -> {
                       expressions.add(PsExpression(it.expression))
                    }
                }
            }
            return PsBlock(
                block.line,
                primaryProperties,
                expressions
            )
        }
    }
}
