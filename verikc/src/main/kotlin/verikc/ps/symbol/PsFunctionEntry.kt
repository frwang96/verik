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

package verikc.ps.symbol

import verikc.base.SymbolEntry
import verikc.base.ast.Symbol
import verikc.ps.ast.PsExpressionFunction
import verikc.sv.ast.SvExpression

data class PsFunctionExtractorRequest(
    val function: PsExpressionFunction,
    val receiver: SvExpression?,
    val args: List<SvExpression>
)

data class PsFunctionEntry(
    override val symbol: Symbol,
    val extractor: (PsFunctionExtractorRequest) -> SvExpression?
): SymbolEntry
