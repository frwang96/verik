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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.ItReifiedType
import verik.core.it.ItTypeClass
import verik.core.sv.SvReifiedType
import java.util.concurrent.ConcurrentHashMap

class LangTypeTable {

    private val typeMap = ConcurrentHashMap<Symbol, LangType>()
    private val identifierMap = ConcurrentHashMap<String, LangType>()

    fun add(type: LangType) {
        if (typeMap[type.symbol] != null) {
            throw IllegalArgumentException("type ${type.identifier} has already been defined")
        }
        typeMap[type.symbol] = type

        if (identifierMap[type.identifier] != null) {
            throw IllegalArgumentException("type ${type.identifier} has already been defined")
        }
        identifierMap[type.identifier] = type
    }

    fun extract(reifiedType: ItReifiedType, line: Int): SvReifiedType {
        if (reifiedType.typeClass != ItTypeClass.INSTANCE) {
            throw LineException("reified type of type class instance expected", line)
        }
        return getType(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("could not extract reified type $reifiedType", line)
    }

    private fun getType(type: Symbol, line: Int): LangType {
        return typeMap[type]
                ?: throw LineException("type $type has not been defined", line)
    }
}