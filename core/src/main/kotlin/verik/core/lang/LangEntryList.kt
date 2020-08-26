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

import verik.core.base.Symbol
import verik.core.it.ItExpressionFunction
import verik.core.it.ItExpressionOperator
import verik.core.it.ItReifiedType
import verik.core.kt.KtExpressionOperator
import verik.core.sv.SvStatement
import verik.core.sv.SvReifiedType

class LangEntryList {
    val types = ArrayList<LangType>()
    val functions = ArrayList<LangFunction>()
    val operators = ArrayList<LangOperator>()
    val properties = ArrayList<LangProperty>()

    fun addType(
            identifier: String,
            parent: Symbol?,
            extractor: (ItReifiedType) -> SvReifiedType?,
            symbol: Symbol
    ) {
        types.add(LangType(identifier, parent, extractor, symbol))
    }

    fun addFunction(
            identifier: String,
            targetType: Symbol?,
            argTypes: List<Symbol>,
            returnType: Symbol,
            reifier: (ItExpressionFunction) -> Unit,
            extractor: (LangFunctionExtractorRequest) -> SvStatement?,
            symbol: Symbol
    ) {
        functions.add(LangFunction(identifier, targetType, argTypes, returnType, reifier, extractor, symbol))
    }

    fun addOperator(
            identifier: String,
            resolver: (KtExpressionOperator) -> Symbol,
            reifier: (ItExpressionOperator) -> ItReifiedType,
            extractor: (LangOperatorExtractorRequest) -> SvStatement?,
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