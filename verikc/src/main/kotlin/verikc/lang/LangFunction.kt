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

import verikc.base.ast.TypeClass
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.gex.ast.GexExpressionFunction
import verikc.sv.ast.SvExpression
import verikc.sv.table.SvFunctionExtractorRequest

data class LangFunction(
    val identifier: String,
    val receiverTypeSymbol: Symbol?,
    val argTypeSymbols: List<Symbol>,
    val argTypeClasses: List<TypeClass>,
    val isVararg: Boolean,
    val returnTypeSymbol: Symbol,
    val generifier: (GexExpressionFunction) -> TypeGenerified?,
    val extractor: (SvFunctionExtractorRequest) -> SvExpression?,
    val symbol: Symbol
)

class LangFunctionList {

    val functions = ArrayList<LangFunction>()

    fun add(
        identifier: String,
        receiverTypeSymbol: Symbol?,
        argTypeSymbols: List<Symbol>,
        argTypeClasses: List<TypeClass>,
        isVararg: Boolean,
        returnTypeSymbol: Symbol,
        generifier: (GexExpressionFunction) -> TypeGenerified?,
        extractor: (SvFunctionExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        if (argTypeSymbols.size != argTypeClasses.size)
            throw IllegalArgumentException("function type symbols and type classes do not match")

        functions.add(
            LangFunction(
                identifier,
                receiverTypeSymbol,
                argTypeSymbols,
                argTypeClasses,
                isVararg,
                returnTypeSymbol,
                generifier,
                extractor,
                symbol
            )
        )
    }
}