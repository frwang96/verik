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

import verik.core.it.ItTypeInstance
import verik.core.main.LineException
import verik.core.sv.SvTypeInstance
import verik.core.main.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class LangTypeTable {

    private val typeMap = ConcurrentHashMap<Symbol, LangType>()
    private val identifierMap = ConcurrentHashMap<String, LangType>()

    fun add(type: LangType) {
        if (typeMap[type.symbol] != null) {
            throw IllegalArgumentException("type symbol ${type.symbol} has already been defined")
        }
        typeMap[type.symbol] = type

        if (identifierMap[type.identifier] != null) {
            throw IllegalArgumentException("type ${type.identifier} has already been defined")
        }
        identifierMap[type.identifier] = type
    }

    fun resolve(identifier: String): Symbol? {
        return identifierMap[identifier]?.symbol
    }

    fun typeClass(type: Symbol, line: Int): LangTypeClass {
        return getType(type, line).typeClass
    }

    fun typeDual(type: Symbol, line: Int): Symbol {
        return getType(type, line).typeDual
    }

    fun extract(typeInstance: ItTypeInstance, line: Int): SvTypeInstance {
        return getType(typeInstance.type, line).extractor(typeInstance)
                ?: throw LineException("unable to extract type instance", line)
    }

    private fun getType(type: Symbol, line: Int): LangType {
        return typeMap[type]
                ?: throw LineException("type symbol $type has not been defined", line)
    }
}