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

package verik.core.lang

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.ItExpressionOperator
import verik.core.it.ItTypeReified
import verik.core.kt.KtExpressionOperator
import verik.core.sv.SvStatement
import java.util.concurrent.ConcurrentHashMap

class LangOperatorTable {

    private val operatorMap = ConcurrentHashMap<Symbol, LangOperator>()

    fun add(operator: LangOperator) {
        if (operatorMap[operator.symbol] != null) {
            throw IllegalArgumentException("operator ${operator.identifier} has already been defined")
        }
        operatorMap[operator.symbol] = operator
    }

    fun resolve(expression: KtExpressionOperator): Symbol {
        return getOperator(expression.operator, expression).resolver(expression)
    }

    fun reify(expression: ItExpressionOperator): ItTypeReified {
        return getOperator(expression.operator, expression).reifier(expression)
    }

    fun extract(request: LangOperatorExtractorRequest): SvStatement {
        val operator = getOperator(request.operator.operator, request.operator)
        return operator.extractor(request)
                ?: throw LineException("could not extract operator ${request.operator.operator}", request.operator)
    }

    private fun getOperator(operator: Symbol, line: Line): LangOperator {
        return operatorMap[operator]
                ?: throw LineException("operator $operator has not been defined", line)
    }
}