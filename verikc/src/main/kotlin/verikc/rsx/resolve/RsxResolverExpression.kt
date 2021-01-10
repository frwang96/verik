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

package verikc.rsx.resolve

import verikc.base.ast.LineException
import verikc.rsx.ast.*
import verikc.rsx.table.RsxSymbolTable

object RsxResolverExpression {

    fun resolve(expression: RsxExpression, symbolTable: RsxSymbolTable) {
        when (expression) {
            is RsxExpressionFunction -> resolveFunction(expression, symbolTable)
            is RsxExpressionOperator -> TODO()
            is RsxExpressionProperty -> TODO()
            is RsxExpressionString -> TODO()
            is RsxExpressionLiteral -> RsxResolverLiteral.resolve(expression)
        }
        if (expression.typeGenerified == null || expression.expressionClass == null) {
            throw LineException("could not resolve expression", expression.line)
        }
    }

    private fun resolveFunction(expression: RsxExpressionFunction, symbolTable: RsxSymbolTable) {
        expression.receiver?.let { resolve(it, symbolTable) }
        expression.args.forEach { resolve(it, symbolTable) }

        val resolverSymbolResult = symbolTable.resolveFunction(expression)
        expression.functionSymbol = resolverSymbolResult.symbol
        expression.typeGenerified = resolverSymbolResult.typeGenerified
        expression.expressionClass = resolverSymbolResult.expressionClass
    }
}
