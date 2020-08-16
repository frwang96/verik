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

package verik.core.kt.symbol

import verik.core.kt.KtDeclarationProperty
import verik.core.main.LineException
import verik.core.main.symbol.Symbol
import java.util.concurrent.ConcurrentHashMap

class KtScopeTable {

    private val propertyMap = ConcurrentHashMap<String, Symbol>()

    fun addProperty(property: KtDeclarationProperty, line: Int) {
        if (propertyMap[property.identifier] != null) {
            throw LineException("property ${property.identifier} has already been defined in scope table", line)
        }
        propertyMap[property.identifier] = property.symbol
    }

    fun resolveProperty(identifier: String): Symbol? {
        return propertyMap[identifier]
    }
}
