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

package verikc.kt.symbol

import verikc.base.symbol.SymbolEntry
import verikc.base.symbol.Symbol

sealed class KtTypeEntry(
    override val symbol: Symbol,
    open val identifier: String,
    open var parentSymbols: List<Symbol>?,
): SymbolEntry

data class KtTypeEntryRegular(
    override val symbol: Symbol,
    override val identifier: String,
    override var parentSymbols: List<Symbol>?,
    val parentIdentifier: String,
    val scope: Symbol,
): KtTypeEntry(symbol, identifier, parentSymbols)

data class KtTypeEntryLang(
    override val symbol: Symbol,
    override val identifier: String,
    override var parentSymbols: List<Symbol>?,
    val parent: Symbol?
): KtTypeEntry(symbol, identifier, parentSymbols)
