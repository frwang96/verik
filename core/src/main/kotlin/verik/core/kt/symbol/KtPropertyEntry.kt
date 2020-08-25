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
import verik.core.kt.KtDeclarationProperty

sealed class KtPropertyEntry(
        open val symbol: Symbol,
        open val type: Symbol?,
        open val identifier: String
)

data class KtPropertyEntryRegular(
        val property: KtDeclarationProperty
): KtPropertyEntry(property.symbol, property.type, property.identifier)

data class KtPropertyEntryLang(
        override val symbol: Symbol,
        override val type: Symbol,
        override val identifier: String
): KtPropertyEntry(symbol, type, identifier)