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

package verik.core.lang

import verik.core.base.ast.ReifiedType
import verik.core.base.ast.Symbol
import verik.core.base.ast.TypeClass
import verik.core.kt.ast.KtExpressionOperator
import verik.core.ps.symbol.PsFunctionExtractorRequest
import verik.core.ps.symbol.PsOperatorExtractorRequest
import verik.core.rf.ast.RfExpressionFunction
import verik.core.rf.ast.RfExpressionOperator
import verik.core.sv.ast.SvExtractedType
import verik.core.sv.ast.SvStatement

class LangEntryList {
    val types = ArrayList<LangType>()
    val functions = ArrayList<LangFunction>()
    val operators = ArrayList<LangOperator>()
    val properties = ArrayList<LangProperty>()

    fun addType(
            identifier: String,
            parent: Symbol?,
            extractor: (ReifiedType) -> SvExtractedType?,
            symbol: Symbol
    ) {
        types.add(LangType(identifier, parent, extractor, symbol))
    }

    fun addFunction(
            identifier: String,
            receiverType: Symbol?,
            argTypes: List<Symbol>,
            argTypeClasses: List<TypeClass>,
            returnType: Symbol,
            reifier: (RfExpressionFunction) -> ReifiedType?,
            extractor: (PsFunctionExtractorRequest) -> SvStatement?,
            symbol: Symbol
    ) {
        functions.add(LangFunction(
                identifier,
                receiverType,
                argTypes,
                argTypeClasses,
                returnType,
                reifier,
                extractor,
                symbol
        ))
    }

    fun addOperator(
            identifier: String,
            resolver: (KtExpressionOperator) -> Symbol,
            reifier: (RfExpressionOperator) -> ReifiedType?,
            extractor: (PsOperatorExtractorRequest) -> SvStatement?,
            symbol: Symbol
    ) {
        operators.add(LangOperator(identifier, resolver, reifier, extractor, symbol))
    }

    fun addProperty(
            identifier: String,
            type: Symbol,
            symbol: Symbol
    ) {
        properties.add(LangProperty(identifier, type, symbol))
    }
}