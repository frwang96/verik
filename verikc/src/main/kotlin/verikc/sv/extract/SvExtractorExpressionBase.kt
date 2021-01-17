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

package verikc.sv.extract

import verikc.ps.ast.*
import verikc.sv.ast.SvBlock
import verikc.sv.ast.SvExpression
import verikc.sv.table.SvFunctionExtractorRequest
import verikc.sv.table.SvOperatorExtractorRequest
import verikc.sv.table.SvPropertyExtractorRequest
import verikc.sv.table.SvSymbolTable

object SvExtractorExpressionBase {

    fun extract(expression: PsExpression, symbolTable: SvSymbolTable): SvExpression {
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
                SvExtractorExpressionString.extract(expression, symbolTable)
            }
            is PsExpressionLiteral -> {
                SvExtractorExpressionLiteral.extract(expression)
            }
        }
    }

    private fun extractFunction(expression: PsExpressionFunction, symbolTable: SvSymbolTable): SvExpression {
        val receiver = expression.receiver?.let { extract(it, symbolTable) }
        val args = expression.args.map { extract(it, symbolTable) }
        return symbolTable.extractFunction(SvFunctionExtractorRequest(expression, receiver, args))
    }

    private fun extractOperator(expression: PsExpressionOperator, symbolTable: SvSymbolTable): SvExpression {
        val receiver = expression.receiver?.let { extract(it, symbolTable) }
        val args = expression.args.map { extract(it, symbolTable) }
        val blocks = expression.blocks.map { SvBlock(it, symbolTable) }
        return symbolTable.extractOperator(SvOperatorExtractorRequest(expression, receiver, args, blocks))
    }

    private fun extractProperty(expression: PsExpressionProperty, symbolTable: SvSymbolTable): SvExpression {
        val receiver = expression.receiver?.let { extract(it, symbolTable) }
        return symbolTable.extractProperty(SvPropertyExtractorRequest(expression, receiver))
    }
}
