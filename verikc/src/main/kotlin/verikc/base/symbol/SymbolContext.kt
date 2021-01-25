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

package verikc.base.symbol

import verikc.lang.LangDeclaration
import verikc.lang.LangSymbol.SCOPE_LANG
import java.util.concurrent.ConcurrentHashMap

class SymbolContext {

    private var symbolCount = 1

    private val identifierMap = ConcurrentHashMap<Symbol, String>()

    init {
        identifierMap[SCOPE_LANG] = "verik"
        for (type in LangDeclaration.types) {
            identifierMap[type.symbol] = type.identifier
        }
        for (function in LangDeclaration.functions) {
            identifierMap[function.symbol] = function.identifier
        }
        for (operator in LangDeclaration.operators) {
            identifierMap[operator.symbol] = operator.identifier
        }
        for (property in LangDeclaration.properties) {
            identifierMap[property.symbol] = property.identifier
        }
    }

    @Synchronized
    fun registerSymbol(identifier: String): Symbol {
        val symbol = Symbol(symbolCount)
        symbolCount += 1
        identifierMap[symbol] = identifier
        return symbol
    }

    fun identifier(symbol: Symbol): String? {
        return identifierMap[symbol]
    }
}
