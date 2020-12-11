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

import verikc.base.ast.Symbol
import verikc.base.ast.TypeClass
import verikc.base.ast.TypeReified
import verikc.kt.ast.KtExpressionOperator
import verikc.ps.symbol.PsFunctionExtractorRequest
import verikc.ps.symbol.PsOperatorExtractorRequest
import verikc.rf.ast.RfExpressionFunction
import verikc.rf.ast.RfExpressionOperator
import verikc.sv.ast.SvExpression
import verikc.sv.ast.SvTypeExtracted

class LangEntryList {
    val types = ArrayList<LangType>()
    val functions = ArrayList<LangFunction>()
    val operators = ArrayList<LangOperator>()
    val properties = ArrayList<LangProperty>()

    fun addType(
        identifier: String,
        parentSymbol: Symbol?,
        extractor: (TypeReified) -> SvTypeExtracted?,
        symbol: Symbol
    ) {
        types.add(LangType(identifier, parentSymbol, extractor, symbol))
    }

    fun addFunction(
        identifier: String,
        receiverTypeSymbol: Symbol?,
        argTypeSymbols: List<Symbol>,
        argTypeClasses: List<TypeClass>,
        returnTypeSymbol: Symbol,
        reifier: (RfExpressionFunction) -> TypeReified?,
        extractor: (PsFunctionExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        functions.add(
            LangFunction(
                identifier,
                receiverTypeSymbol,
                argTypeSymbols,
                argTypeClasses,
                returnTypeSymbol,
                reifier,
                extractor,
                symbol
            )
        )
    }

    fun addOperator(
        identifier: String,
        resolver: (KtExpressionOperator) -> Symbol,
        reifier: (RfExpressionOperator) -> TypeReified?,
        extractor: (PsOperatorExtractorRequest) -> SvExpression?,
        symbol: Symbol
    ) {
        operators.add(LangOperator(identifier, resolver, reifier, extractor, symbol))
    }

    fun addProperty(
        identifier: String,
        typeSymbol: Symbol,
        symbol: Symbol
    ) {
        properties.add(LangProperty(identifier, typeSymbol, symbol))
    }
}
