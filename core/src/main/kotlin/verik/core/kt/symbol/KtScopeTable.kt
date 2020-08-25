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

import verik.core.base.LineException
import verik.core.base.Symbol
import verik.core.kt.KtDeclarationProperty

class KtScopeTable(
        private val scope: Symbol
) {
    private val properties = ArrayList<Pair<String, Symbol>>()
    private val types = ArrayList<Pair<String, Symbol>>()

    fun addProperty(property: KtDeclarationProperty) {
        if (properties.any { it.first == property.identifier }) {
            throw LineException("property ${property.identifier} has already been defined in scope $scope", property)
        }
        properties.add(Pair(property.identifier, property.symbol))
    }

    fun addType(type: KtTypeEntry, line: Int) {
        if (types.any { it.first == type.identifier }) {
            throw LineException("type ${type.identifier} has already been defined in scope $scope", line)
        }
        types.add(Pair(type.identifier, type.symbol))
    }

    fun resolveProperty(identifier: String): Symbol? {
        return properties.find { it.first == identifier }?.second
    }

    fun resolveType(identifier: String): Symbol? {
        return types.find { it.first == identifier }?.second
    }
}
