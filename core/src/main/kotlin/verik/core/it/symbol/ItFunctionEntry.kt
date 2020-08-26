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

package verik.core.it.symbol

import verik.core.base.Symbol
import verik.core.base.SymbolEntry
import verik.core.it.ItExpressionFunction
import verik.core.sv.SvExpression
import verik.core.sv.SvStatement

data class ItFunctionExtractorRequest(
        val function: ItExpressionFunction,
        val target: SvExpression?,
        val args: List<SvExpression>
)

data class ItFunctionEntry(
        override val symbol: Symbol,
        val reifier: (ItExpressionFunction) -> Unit,
        val extractor: (ItFunctionExtractorRequest) -> SvStatement?
): SymbolEntry