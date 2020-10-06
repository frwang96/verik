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

package verik.core.rf.extract

import verik.core.base.LineException
import verik.core.rf.*
import verik.core.rf.symbol.RfFunctionExtractorRequest
import verik.core.rf.symbol.RfOperatorExtractorRequest
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.SvExpression
import verik.core.sv.SvStatement
import verik.core.sv.SvStatementExpression

object RfExpressionExtractor {

    fun extract(expression: RfExpression, symbolTable: RfSymbolTable): SvStatement {
        return when(expression) {
            is RfExpressionFunction -> {
                extractFunction(expression, symbolTable)
            }
            is RfExpressionOperator -> {
                extractOperator(expression, symbolTable)
            }
            is RfExpressionProperty -> {
                extractProperty(expression, symbolTable)
            }
            is RfExpressionString -> {
                SvStatementExpression(RfExpressionExtractorString.extract(expression, symbolTable))
            }
            is RfExpressionLiteral -> {
                SvStatementExpression(RfExpressionExtractorLiteral.extract(expression))
            }
        }
    }

    private fun extractFunction(function: RfExpressionFunction, symbolTable: RfSymbolTable): SvStatement {
        val receiver = function.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = function.args.map { unwrap(extract(it, symbolTable)) }
        return symbolTable.extractFunction(RfFunctionExtractorRequest(
                function,
                receiver,
                args
        ))
    }

    private fun extractOperator(operator: RfExpressionOperator, symbolTable: RfSymbolTable): SvStatement {
        val receiver = operator.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = operator.args.map { unwrap(extract(it, symbolTable)) }
        val blocks = operator.blocks.map { it.extract(symbolTable) }
        return symbolTable.extractOperator(RfOperatorExtractorRequest(
                operator,
                receiver,
                args,
                blocks
        ))
    }

    private fun extractProperty(property: RfExpressionProperty, symbolTable: RfSymbolTable): SvStatement {
        if (property.receiver != null) {
            throw LineException("extraction of property with receiver expression not supported", property)
        }
        return symbolTable.extractProperty(property)
    }

    private fun unwrap(statement: SvStatement): SvExpression {
        return if (statement is SvStatementExpression) statement.expression
        else throw LineException("expected expression from extraction", statement)
    }
}