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

package verikc.lang

import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.gex.ast.GexExpressionOperator
import verikc.rs.table.RsOperatorResolverRequest
import verikc.sv.ast.SvExpression
import verikc.sv.table.SvOperatorExtractorRequest

data class LangOperator(
    val identifier: String,
    val resolver: (RsOperatorResolverRequest) -> Symbol,
    val generifier: (GexExpressionOperator) -> TypeGenerified?,
    val extractor: (SvOperatorExtractorRequest) -> SvExpression?,
    val symbol: Symbol
)

class LangOperatorList {

    val operators = ArrayList<LangOperator>()

    fun add(
        identifier: String,
        resolver: (RsOperatorResolverRequest) -> Symbol,
        generifier: (GexExpressionOperator) -> TypeGenerified?,
        extractor: (SvOperatorExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        operators.add(LangOperator(identifier, resolver, generifier, extractor, symbol))
    }
}