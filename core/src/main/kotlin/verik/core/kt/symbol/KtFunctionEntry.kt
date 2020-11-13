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

import verik.core.base.SymbolEntry
import verik.core.base.ast.Symbol

data class KtFunctionEntry(
        override val symbol: Symbol,
        val identifier: String,
        val returnType: Symbol,
        val argTypes: List<Symbol>
): SymbolEntry {

    fun matches(argsParents: List<List<Symbol>>): Boolean {
        if (argsParents.size != argTypes.size) return false
        argTypes.zip(argsParents).forEach {
            if (it.first !in it.second) return false
        }
        return true
    }
}
