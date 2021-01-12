/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.rs.table

import verikc.base.ast.ExpressionClass
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry
import verikc.rs.ast.RsExpressionFunction

sealed class RsFunctionEntry(
    override val symbol: Symbol,
    open val identifier: String,
    open val argTypeSymbols: List<Symbol>,
    open val argExpressionClasses: List<ExpressionClass>,
    open val isVararg: Boolean,
    open val returnExpressionClass: ExpressionClass
): SymbolEntry {

    fun getArgExpressionClass(index: Int): ExpressionClass {
        return if (isVararg && index >= argExpressionClasses.size) argExpressionClasses.last()
        else argExpressionClasses[index]
    }
}

data class RsFunctionEntryLang(
    override val symbol: Symbol,
    override val identifier: String,
    override val argTypeSymbols: List<Symbol>,
    override val argExpressionClasses: List<ExpressionClass>,
    override val isVararg: Boolean,
    override val returnExpressionClass: ExpressionClass,
    val resolver: (RsFunctionResolverRequest) -> TypeGenerified?
): RsFunctionEntry(symbol, identifier, argTypeSymbols, argExpressionClasses, isVararg, returnExpressionClass)

data class RsFunctionEntryRegular(
    override val symbol: Symbol,
    override val identifier: String,
    override val argTypeSymbols: List<Symbol>,
    override val argExpressionClasses: List<ExpressionClass>,
    override val isVararg: Boolean,
    override val returnExpressionClass: ExpressionClass,
    val argTypesGenerified: List<TypeGenerified>,
    val returnTypeGenerified: TypeGenerified
): RsFunctionEntry(symbol, identifier, argTypeSymbols, argExpressionClasses, isVararg, returnExpressionClass)

data class RsFunctionResolverRequest(
    val expression: RsExpressionFunction,
    val symbolTable: RsSymbolTable
)