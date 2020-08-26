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
import verik.core.it.ItReifiedType
import verik.core.it.symbol.ItFunctionExtractorRequest
import verik.core.sv.SvStatement

data class LangFunction(
        val identifier: String,
        val targetType: Symbol?,
        val argTypes: List<Symbol>,
        val returnType: Symbol,
        val reifier: (ItExpressionFunction) -> ItReifiedType?,
        val extractor: (ItFunctionExtractorRequest) -> SvStatement?,
        val symbol: Symbol
)
