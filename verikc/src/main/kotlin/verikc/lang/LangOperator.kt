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

import verikc.base.ast.ExpressionClass
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.ge.ast.GeExpressionOperator
import verikc.rs.table.RsOperatorResolverRequest
import verikc.rsx.table.RsxOperatorResolverRequest
import verikc.sv.ast.SvExpression
import verikc.sv.table.SvOperatorExtractorRequest

data class LangOperator(
    val identifier: String,
    val returnExpressionClass: ExpressionClass,
    val resolver: (RsOperatorResolverRequest) -> Symbol,
    val generifier: (GeExpressionOperator) -> TypeGenerified?,
    val resolverGenerifier: (RsxOperatorResolverRequest) -> TypeGenerified?,
    val extractor: (SvOperatorExtractorRequest) -> SvExpression?,
    val symbol: Symbol
)

class LangOperatorList {

    val operators = ArrayList<LangOperator>()

    fun add(
        identifier: String,
        returnExpressionClass: ExpressionClass,
        resolver: (RsOperatorResolverRequest) -> Symbol,
        generifier: (GeExpressionOperator) -> TypeGenerified?,
        resolverGenerifier: (RsxOperatorResolverRequest) -> TypeGenerified?,
        extractor: (SvOperatorExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        operators.add(
            LangOperator(
                identifier,
                returnExpressionClass,
                resolver,
                generifier,
                resolverGenerifier,
                extractor,
                symbol
            )
        )
    }
}