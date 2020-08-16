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

package verik.core.lang

import verik.core.it.ItTypeInstance
import verik.core.sv.SvTypeInstance
import verik.core.main.symbol.Symbol

enum class LangTypeClass {
    UNIT,
    INT,
    TYPE,
    INSTANCE
}

data class LangType(
        val symbol: Symbol,
        val typeClass: LangTypeClass,
        val typeDual: Symbol,
        val extractor: (ItTypeInstance) -> SvTypeInstance?,
        val identifier: String
)