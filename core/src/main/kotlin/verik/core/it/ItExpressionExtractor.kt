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

package verik.core.it

import verik.core.base.LineException
import verik.core.it.symbol.ItSymbolTable
import verik.core.lang.Lang
import verik.core.lang.LangFunctionExtractorRequest
import verik.core.lang.LangSymbol.TYPE_INT
import verik.core.sv.SvExpression
import verik.core.sv.SvExpressionFunction
import verik.core.sv.SvExpressionLiteral
import verik.core.sv.SvExpressionProperty

object ItExpressionExtractor {

    fun extract(expression: ItExpression, symbolTable: ItSymbolTable): SvExpression {
        return when(expression) {
            is ItExpressionFunction -> extractFunction(expression, symbolTable)
            is ItExpressionOperator -> throw LineException("extraction of operator expressions is not supported", expression)
            is ItExpressionProperty -> extractProperty(expression, symbolTable)
            is ItExpressionString -> ItExpressionExtractorString.extract(expression, symbolTable)
            is ItExpressionLiteral -> extractLiteral(expression)
        }
    }

    private fun extractFunction(function: ItExpressionFunction, symbolTable: ItSymbolTable): SvExpressionFunction {
        val target = function.target?.let { extract(it, symbolTable) }
        val args = function.args.map { extract(it, symbolTable) }
        return Lang.functionTable.extract(LangFunctionExtractorRequest(
                function,
                target,
                args
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

    private fun extractLiteral(literal: ItExpressionLiteral): SvExpressionLiteral {
        val typeReified = literal.typeReified
                ?: throw LineException("literal expression has not been reified", literal)
        val string = when (typeReified.type) {
            TYPE_INT -> literal.value.toInt().toString()
            else -> throw LineException("literal type not supported", literal)
        }
        return SvExpressionLiteral(
                literal.line,
                string
        )
    }
}