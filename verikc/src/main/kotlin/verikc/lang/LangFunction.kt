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
import verikc.ge.ast.GeExpressionFunction
import verikc.rsx.ast.RsxExpressionFunction
import verikc.sv.ast.SvExpression
import verikc.sv.table.SvFunctionExtractorRequest

data class LangFunction(
    val identifier: String,
    val receiverTypeSymbol: Symbol?,
    val argTypeSymbols: List<Symbol>,
    val argExpressionClasses: List<ExpressionClass>,
    val isVararg: Boolean,
    val returnTypeSymbol: Symbol,
    val returnExpressionClass: ExpressionClass,
    val generifier: (GeExpressionFunction) -> TypeGenerified?,
    val resolver: (RsxExpressionFunction) -> TypeGenerified?,
    val extractor: (SvFunctionExtractorRequest) -> SvExpression?,
    val symbol: Symbol
)

class LangFunctionList {

    val functions = ArrayList<LangFunction>()

    fun add(
        identifier: String,
        receiverTypeSymbol: Symbol?,
        argTypeSymbols: List<Symbol>,
        argExpressionClasses: List<ExpressionClass>,
        isVararg: Boolean,
        returnTypeSymbol: Symbol,
        returnExpressionClass: ExpressionClass,
        generifier: (GeExpressionFunction) -> TypeGenerified?,
        resolver: (RsxExpressionFunction) -> TypeGenerified?,
        extractor: (SvFunctionExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        if (argTypeSymbols.size != argExpressionClasses.size)
            throw IllegalArgumentException("function argument type symbols and expression classes do not match")

        functions.add(
            LangFunction(
                identifier,
                receiverTypeSymbol,
                argTypeSymbols,
                argExpressionClasses,
                isVararg,
                returnTypeSymbol,
                returnExpressionClass,
                generifier,
                resolver,
                extractor,
                symbol
            )
        )
    }
}