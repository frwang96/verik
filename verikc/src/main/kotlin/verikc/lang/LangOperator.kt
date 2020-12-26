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
import verikc.kt.ast.KtExpressionOperator
import verikc.ps.symbol.PsOperatorExtractorRequest
import verikc.rf.ast.RfExpressionOperator
import verikc.sv.ast.SvExpression

data class LangOperator(
    val identifier: String,
    val resolver: (KtExpressionOperator) -> Symbol,
    val reifier: (RfExpressionOperator) -> TypeReified?,
    val extractor: (PsOperatorExtractorRequest) -> SvExpression?,
    val symbol: Symbol
)

class LangOperatorList {

    val operators = ArrayList<LangOperator>()

    fun add(
        identifier: String,
        resolver: (KtExpressionOperator) -> Symbol,
        reifier: (RfExpressionOperator) -> TypeReified?,
        extractor: (PsOperatorExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        operators.add(LangOperator(identifier, resolver, reifier, extractor, symbol))
    }
}