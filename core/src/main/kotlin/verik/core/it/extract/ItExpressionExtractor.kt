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
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.Lang
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.lang.LangOperatorExtractorRequest
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
                extractProperty(expression, symbolTable).let {
                    SvStatementExpression(it.line, it)
                }
            }
            is ItExpressionString -> {
                ItExpressionExtractorString.extract(expression, symbolTable).let {
                    SvStatementExpression(it.line, it)
                }
            }
            is ItExpressionLiteral -> {
                ItExpressionExtractorLiteral.extract(expression).let {
                    SvStatementExpression(it.line, it)
                }
            }
        }
    }

    private fun extractFunction(function: ItExpressionFunction, symbolTable: ItSymbolTable): SvStatement {
        val target = function.target?.let { unwrap(extract(it, symbolTable)) }
        val args = function.args.map { unwrap(extract(it, symbolTable)) }
        return Lang.functionTable.extract(LangFunctionExtractorRequest(
                function,
                target,
                args
        ))
    }

    private fun extractOperator(operator: ItExpressionOperator, symbolTable: ItSymbolTable): SvStatement {
        val target = operator.target?.let { unwrap(extract(it, symbolTable)) }
        val args = operator.args.map { unwrap(extract(it, symbolTable)) }
        val blocks = operator.blocks.map { it.extract(symbolTable) }
        return Lang.operatorTable.extract(LangOperatorExtractorRequest(
                operator,
                target,
                args,
                blocks
        ))
    }

    private fun extractProperty(property: ItExpressionProperty, symbolTable: ItSymbolTable): SvExpressionProperty {
        if (property.target != null) {
            throw LineException("extraction of property with target expression not supported", property)
        }
        val resolvedProperty = symbolTable.getProperty(property)
        return SvExpressionProperty(
                property.line,
                null,
                resolvedProperty.identifier
        )
    }

    private fun unwrap(statement: SvStatement): SvExpression {
        return if (statement is SvStatementExpression) statement.expression
        else throw LineException("expected expression from extraction", statement)
    }
}