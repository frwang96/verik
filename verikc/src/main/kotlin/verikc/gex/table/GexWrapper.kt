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

package verikc.gex.table

import verikc.base.ast.LineException
import verikc.ge.ast.*
import verikc.gex.ast.*

object GexWrapper {

    fun toGe(expression: GexExpression): GeExpression {
        return when (expression) {
            is GexExpressionFunction -> {
                GeExpressionFunction(
                    expression.line,
                    expression.typeSymbol,
                    expression.typeGenerified,
                    expression.functionSymbol,
                    expression.receiver?.let { toGe(it) },
                    expression.args.map{ toGe(it) }
                )
            }
            is GexExpressionOperator -> {
                GeExpressionOperator(
                    expression.line,
                    expression.typeSymbol,
                    expression.typeGenerified,
                    expression.operatorSymbol,
                    expression.receiver?.let { toGe(it) },
                    expression.args.map{ toGe(it) },
                    expression.blocks.map { toGe(it) }
                )
            }
            is GexExpressionProperty -> {
                GeExpressionProperty(
                    expression.line,
                    expression.typeSymbol,
                    expression.typeGenerified,
                    expression.propertySymbol,
                    expression.receiver?.let { toGe(it) },
                )
            }
            is GexExpressionString -> {
                throw LineException("not supported", expression.line)
            }
            is GexExpressionLiteral -> {
                GeExpressionLiteral(
                    expression.line,
                    expression.typeSymbol,
                    expression.typeGenerified,
                    expression.value
                )
            }
        }
    }

    private fun toGe(block: GexBlock): GeBlock {
        return GeBlock(
            block.line,
            block.statements.map { toGe(it) }
        )
    }

    private fun toGe(statement: GexStatement): GeStatement {
        return when (statement) {
            is GexStatementDeclaration -> {
                GeStatementDeclaration(toGe(statement.property))
            }
            is GexStatementExpression -> {
                GeStatementExpression(toGe(statement.expression))
            }
        }
    }

    private fun toGe(property: GexProperty): GePrimaryProperty {
        return GePrimaryProperty(
            property.line,
            property.identifier,
            property.symbol,
            property.typeSymbol,
            property.typeGenerified,
            property.expression?.let { toGe(it) }
        )
    }
}