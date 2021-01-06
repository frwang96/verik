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

package verikc.ge.symbol

import verikc.base.ast.TypeClass
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry
import verikc.ge.ast.GeExpressionFunction

sealed class GeFunctionEntry(
    override val symbol: Symbol
): SymbolEntry

data class GeFunctionLangEntry(
    override val symbol: Symbol,
    val argTypeClasses: List<TypeClass>,
    val isVararg: Boolean,
    val reifier: (GeExpressionFunction) -> TypeReified?
): GeFunctionEntry(symbol) {

    fun getArgTypeClass(index: Int): TypeClass {
        return if (isVararg && index >= argTypeClasses.size) argTypeClasses.last()
        else argTypeClasses[index]
    }
}

data class GeFunctionRegularEntry(
    override val symbol: Symbol,
    val argTypesReified: List<TypeReified>,
    val returnTypeReified: TypeReified
): GeFunctionEntry(symbol)