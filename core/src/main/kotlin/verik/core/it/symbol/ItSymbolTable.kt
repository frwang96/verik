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

package verik.core.it.symbol

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.it.ItExpressionProperty
import verik.core.it.ItProperty
import verik.core.it.ItReifiedType
import verik.core.lang.Lang
import verik.core.sv.SvReifiedType
import java.util.concurrent.ConcurrentHashMap

class ItSymbolTable {

    private val typeEntryMap = ConcurrentHashMap<Symbol, ItTypeEntry>()
    private val propertyMap = ConcurrentHashMap<Symbol, ItProperty>()

    init {
        for (type in Lang.types) {
            val typeEntry = ItTypeEntry(
                    type.symbol,
                    type.extractor
            )
            addTypeEntry(typeEntry, 0)
        }
    }

    fun addProperty(property: ItProperty) {
        if (propertyMap[property.symbol] != null) {
            throw LineException("property ${property.identifier} has already been defined", property)
        }
        propertyMap[property.symbol] = property
    }

    fun extractType(reifiedType: ItReifiedType, line: Int): SvReifiedType {
        return getType(reifiedType.type, line).extractor(reifiedType)
                ?: throw LineException("could not extract type $reifiedType", line)
    }

    fun getProperty(property: ItExpressionProperty): ItProperty {
        return propertyMap[property.property]
                ?: throw LineException("property ${property.property} has not been defined", property)
    }

    private fun addTypeEntry(typeEntry: ItTypeEntry, line: Int) {
        if (typeEntryMap[typeEntry.symbol] != null) {
            throw LineException("type ${typeEntry.symbol} has already been defined", line)
        }
        typeEntryMap[typeEntry.symbol] = typeEntry
    }

    private fun getType(type: Symbol, line: Int): ItTypeEntry {
        return typeEntryMap[type]
                ?: throw LineException("type $type has not been defined", line)
    }
}