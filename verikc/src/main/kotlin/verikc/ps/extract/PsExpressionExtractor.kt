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

package verikc.ps.extract

import verikc.base.ast.LineException
import verikc.ps.ast.*
import verikc.ps.symbol.PsFunctionExtractorRequest
import verikc.ps.symbol.PsOperatorExtractorRequest
import verikc.ps.symbol.PsSymbolTable
import verikc.sv.ast.SvExpression

object PsExpressionExtractor {

    fun extract(expression: PsExpression, symbolTable: PsSymbolTable): SvExpression {
        return when (expression) {
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
                PsExpressionExtractorString.extract(expression, symbolTable)
            }
            is PsExpressionLiteral -> {
                PsExpressionExtractorLiteral.extract(expression)
            }
        }
    }

    private fun extractFunction(function: PsExpressionFunction, symbolTable: PsSymbolTable): SvExpression {
        val receiver = function.receiver?.let { extract(it, symbolTable) }
        val args = function.args.map { extract(it, symbolTable) }
        return symbolTable.extractFunction(PsFunctionExtractorRequest(function, receiver, args))
    }

    private fun extractOperator(operator: PsExpressionOperator, symbolTable: PsSymbolTable): SvExpression {
        val receiver = operator.receiver?.let { extract(it, symbolTable) }
        val args = operator.args.map { extract(it, symbolTable) }
        val blocks = operator.blocks.map { it.extract(symbolTable) }
        return symbolTable.extractOperator(PsOperatorExtractorRequest(operator, receiver, args, blocks))
    }

    private fun extractProperty(property: PsExpressionProperty, symbolTable: PsSymbolTable): SvExpression {
        if (property.receiver != null) {
            throw LineException("extraction of property with receiver expression not supported", property.line)
        }
        return symbolTable.extractProperty(property)
    }
}
