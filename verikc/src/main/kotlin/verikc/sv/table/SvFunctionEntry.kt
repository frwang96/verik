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

package verikc.sv.table

import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolEntry
import verikc.ps.ast.PsExpressionFunction
import verikc.sv.ast.SvExpression

sealed class SvFunctionEntry(
    override val symbol: Symbol,
): SymbolEntry

data class SvFunctionLangEntry(
    override val symbol: Symbol,
    val extractor: (SvFunctionExtractorRequest) -> SvExpression?
): SvFunctionEntry(symbol)

data class SvFunctionExtractorRequest(
    val expression: PsExpressionFunction,
    val receiver: SvExpression?,
    val args: List<SvExpression>
)

data class SvFunctionRegularEntry(
    override val symbol: Symbol,
    val identifier: String
): SvFunctionEntry(symbol)