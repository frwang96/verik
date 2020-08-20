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
import java.util.concurrent.ConcurrentHashMap

class ItSymbolTable {

    private val propertyMap = ConcurrentHashMap<Symbol, ItProperty>()

    fun addProperty(property: ItProperty) {
        if (propertyMap[property.symbol] != null) {
            throw LineException("property symbol ${property.symbol} has already been defined", property)
        }
        propertyMap[property.symbol] = property
    }

    fun getProperty(property: ItExpressionProperty): ItProperty {
        return propertyMap[property.property]
                ?: throw LineException("property symbol ${property.property} has not been defined", property)
    }
}