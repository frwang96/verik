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

package verik.core.it.extract

import verik.core.base.LineException
import verik.core.it.*
import verik.core.it.symbol.ItFunctionExtractorRequest
import verik.core.it.symbol.ItOperatorExtractorRequest
import verik.core.it.symbol.ItSymbolTable
import verik.core.sv.SvExpression
import verik.core.sv.SvExpressionProperty
import verik.core.sv.SvStatement
import verik.core.sv.SvStatementExpression

object ItExpressionExtractor {

    fun extract(expression: ItExpression, symbolTable: ItSymbolTable): SvStatement {
        return when(expression) {
            is ItExpressionFunction -> {
                extractFunction(expression, symbolTable)
            }
            is ItExpressionOperator -> {
                extractOperator(expression, symbolTable)
            }
            is ItExpressionProperty -> {
                SvStatementExpression(extractProperty(expression, symbolTable))
            }
            is ItExpressionString -> {
                SvStatementExpression(ItExpressionExtractorString.extract(expression, symbolTable))
            }
            is ItExpressionLiteral -> {
                SvStatementExpression(ItExpressionExtractorLiteral.extract(expression))
            }
        }
    }

    private fun extractFunction(function: ItExpressionFunction, symbolTable: ItSymbolTable): SvStatement {
        val receiver = function.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = function.args.map { unwrap(extract(it, symbolTable)) }
        return symbolTable.extractFunction(ItFunctionExtractorRequest(
                function,
                receiver,
                args
        ))
    }

    private fun extractOperator(operator: ItExpressionOperator, symbolTable: ItSymbolTable): SvStatement {
        val receiver = operator.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = operator.args.map { unwrap(extract(it, symbolTable)) }
        val blocks = operator.blocks.map { it.extract(symbolTable) }
        return symbolTable.extractOperator(ItOperatorExtractorRequest(
                operator,
                receiver,
                args,
                blocks
        ))
    }

    private fun extractProperty(property: ItExpressionProperty, symbolTable: ItSymbolTable): SvExpressionProperty {
        if (property.receiver != null) {
            throw LineException("extraction of property with receiver expression not supported", property)
        }
        return SvExpressionProperty(
                property.line,
                null,
                symbolTable.extractProperty(property)
        )
    }

    private fun unwrap(statement: SvStatement): SvExpression {
        return if (statement is SvStatementExpression) statement.expression
        else throw LineException("expected expression from extraction", statement)
    }
}