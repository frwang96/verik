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

package verik.core.ps.extract

import verik.core.base.ast.LineException
import verik.core.ps.ast.*
import verik.core.ps.symbol.PsFunctionExtractorRequest
import verik.core.ps.symbol.PsOperatorExtractorRequest
import verik.core.ps.symbol.PsSymbolTable
import verik.core.sv.ast.SvExpression
import verik.core.sv.ast.SvStatement
import verik.core.sv.ast.SvStatementExpression

// TODO remove annotation
@Suppress("DuplicatedCode")
object PsExpressionExtractor {

    fun extract(expression: PsExpression, symbolTable: PsSymbolTable): SvStatement {
        return when(expression) {
            is PsExpressionFunction -> {
                extractFunction(expression, symbolTable)
            }
            is PsExpressionOperator -> {
                extractOperator(expression, symbolTable)
            }
            is PsExpressionProperty -> {
                extractProperty(expression, symbolTable)
            }
            is PsExpressionString -> {
                SvStatementExpression(PsExpressionExtractorString.extract(expression, symbolTable))
            }
            is PsExpressionLiteral -> {
                SvStatementExpression(PsExpressionExtractorLiteral.extract(expression))
            }
        }
    }

    private fun extractFunction(function: PsExpressionFunction, symbolTable: PsSymbolTable): SvStatement {
        val receiver = function.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = function.args.map { unwrap(extract(it, symbolTable)) }
        return symbolTable.extractFunction(PsFunctionExtractorRequest(
                function,
                receiver,
                args
        ))
    }

    private fun extractOperator(operator: PsExpressionOperator, symbolTable: PsSymbolTable): SvStatement {
        val receiver = operator.receiver?.let { unwrap(extract(it, symbolTable)) }
        val args = operator.args.map { unwrap(extract(it, symbolTable)) }
        val blocks = operator.blocks.map { it.extract(symbolTable) }
        return symbolTable.extractOperator(PsOperatorExtractorRequest(
                operator,
                receiver,
                args,
                blocks
        ))
    }

    private fun extractProperty(property: PsExpressionProperty, symbolTable: PsSymbolTable): SvStatement {
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
