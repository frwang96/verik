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

package verikc.rs.table

import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry

sealed class RsTypeEntry(
    override val symbol: Symbol,
    open val identifier: String,
    open var parentTypeSymbols: List<Symbol>?,
): SymbolEntry

data class RsTypeEntryRegular(
    override val symbol: Symbol,
    override val identifier: String,
    override var parentTypeSymbols: List<Symbol>?,
    val parentIdentifier: String,
    val scope: Symbol,
): RsTypeEntry(symbol, identifier, parentTypeSymbols)

data class RsTypeEntryLang(
    override val symbol: Symbol,
    override val identifier: String,
    override var parentTypeSymbols: List<Symbol>?,
    val parent: Symbol?
): RsTypeEntry(symbol, identifier, parentTypeSymbols)
