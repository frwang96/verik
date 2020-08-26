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
import verik.core.it.ItExpressionOperator
import verik.core.it.ItReifiedType
import verik.core.kt.KtExpressionOperator
import verik.core.sv.SvBlock
import verik.core.sv.SvExpression
import verik.core.sv.SvStatement

data class LangOperatorExtractorRequest(
        val operator: ItExpressionOperator,
        val target: SvExpression?,
        val args: List<SvExpression>,
        val blocks: List<SvBlock>
)

data class LangOperator(
        val identifier: String,
        val resolver: (KtExpressionOperator) -> Symbol,
        val reifier: (ItExpressionOperator) -> ItReifiedType,
        val extractor: (LangOperatorExtractorRequest) -> SvStatement?,
        val symbol: Symbol
)