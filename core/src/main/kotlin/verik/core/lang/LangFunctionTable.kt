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

import verik.core.it.ItExpressionFunction
import verik.core.main.LineException
import verik.core.sv.SvExpressionFunction
import verik.core.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class LangFunctionTable {

    private val functionMap = ConcurrentHashMap<Symbol, LangFunction>()
    private val identifierMap = ConcurrentHashMap<String, ArrayList<LangFunction>>()

    fun add(function: LangFunction) {
        if (functionMap[function.symbol] != null) {
            throw IllegalArgumentException("function symbol ${function.symbol} has already been defined")
        }
        functionMap[function.symbol] = function

        val functions = identifierMap[function.identifier]
        if (functions != null) {
            functions.add(function)
        } else {
            identifierMap[function.identifier] = ArrayList(listOf(function))
        }
    }

    fun match(identifier: String, argTypes: List<Symbol>): LangFunctionMatch {
        val functions = identifierMap[identifier]
        return if (functions != null) {
            val matches = functions.filter { it.argTypes == argTypes }
            when (matches.size) {
                0 -> LangFunctionMatchNone
                1 -> LangFunctionMatchSingle(matches[0].symbol, matches[0].returnType)
                else -> LangFunctionMatchMultiple
            }
        } else LangFunctionMatchNone
    }

    fun instantiate(request: LangFunctionInstantiatorRequest): ItExpressionFunction {
        val function = getFunction(request.function.function)
        return function.instantiator(request)
    }

    fun extract(request: LangFunctionExtractorRequest): SvExpressionFunction {
        val function = getFunction(request.function.function)
        return function.extractor(request)
                ?: throw LineException("could not extract function", request.function)
    }

    private fun getFunction(function: Symbol): LangFunction {
        return functionMap[function]
                ?: throw IllegalArgumentException("function symbol $function could not be found")
    }
}