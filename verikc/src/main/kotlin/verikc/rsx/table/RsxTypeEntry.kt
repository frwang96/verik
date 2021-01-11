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

package verikc.rsx.table

import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry

sealed class RsxTypeEntry(
    override val symbol: Symbol,
    open val identifier: String,
    open val hasTypeParameters: Boolean,
    open var parentTypeSymbols: List<Symbol>?
): SymbolEntry

data class RsxTypeEntryLang(
    override val symbol: Symbol,
    override val identifier: String,
    override val hasTypeParameters: Boolean,
    override var parentTypeSymbols: List<Symbol>?,
    val parentTypeSymbol: Symbol?
): RsxTypeEntry(symbol, identifier, hasTypeParameters, parentTypeSymbols)

data class RsxTypeEntryRegular(
    override val symbol: Symbol,
    override val identifier: String,
    override val hasTypeParameters: Boolean,
    override var parentTypeSymbols: List<Symbol>?,
    val parentIdentifier: String,
    val scope: Symbol
): RsxTypeEntry(symbol, identifier, hasTypeParameters, parentTypeSymbols)
