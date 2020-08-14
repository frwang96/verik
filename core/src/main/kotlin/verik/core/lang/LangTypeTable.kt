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

import verik.core.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class LangTypeTable {

    private val typeMap = ConcurrentHashMap<Symbol, LangType>()
    private val identifierMap = ConcurrentHashMap<String, LangType>()

    fun add(type: LangType) {
        if (typeMap.contains(type.symbol)) {
            throw IllegalArgumentException("type symbol ${type.symbol} has already been defined")
        }
        typeMap[type.symbol] = type

        if (identifierMap.contains(type.identifier)) {
            throw IllegalArgumentException("type ${type.identifier} has already been defined")
        }
        identifierMap[type.identifier] = type
    }

    fun resolve(identifier: String): Symbol? {
        return identifierMap[identifier]?.symbol
    }
}