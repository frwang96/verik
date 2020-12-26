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

package verikc.lang

import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.sv.ast.SvTypeExtracted

data class LangType(
    val identifier: String,
    val parentSymbol: Symbol?,
    val extractor: (TypeReified) -> SvTypeExtracted?,
    val symbol: Symbol
)

class LangTypeList {

    val types = ArrayList<LangType>()

    fun add(
        identifier: String,
        parentSymbol: Symbol?,
        extractor: (TypeReified) -> SvTypeExtracted?,
        symbol: Symbol
    ) {
        types.add(LangType(identifier, parentSymbol, extractor, symbol))
    }
}