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

import verik.core.base.Symbol
import verik.core.kt.KtDeclarationFunction

sealed class KtFunctionEntry(
        open val symbol: Symbol,
        open val identifier: String,
        open var returnType: Symbol?
) {

    abstract fun matches(argsParents: List<List<Symbol>>): Boolean
}

data class KtFunctionEntryRegular(
        val function: KtDeclarationFunction
): KtFunctionEntry(function.symbol, function.identifier, function.returnType) {

    override var returnType: Symbol?
        get() = function.returnType
        set(value) { function.returnType = value }

    override fun matches(argsParents: List<List<Symbol>>): Boolean {
        return false
    }
}

data class KtFunctionEntryLang(
        override val symbol: Symbol,
        override val identifier: String,
        override var returnType: Symbol?,
        val argTypes: List<Symbol>
): KtFunctionEntry(symbol, identifier, returnType) {

    override fun matches(argsParents: List<List<Symbol>>): Boolean {
        if (argsParents.size != argTypes.size) return false
        argTypes.zip(argsParents).forEach {
            if (it.first !in it.second) return false
        }
        return true
    }
}
