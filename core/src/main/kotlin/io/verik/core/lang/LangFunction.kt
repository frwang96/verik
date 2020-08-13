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

package io.verik.core.lang

import io.verik.core.svx.SvxExpression
import io.verik.core.symbol.Symbol
import io.verik.core.vkx.VkxExpression
import io.verik.core.vkx.VkxType

data class LangFunctionExtractorEntry(
        val expression: VkxExpression,
        val args: List<SvxExpression>
)

data class LangFunction(
        val symbol: Symbol,
        val argTypes: List<Symbol>,
        val returnType: Symbol,
        val resolver: (List<VkxExpression>) -> VkxType?,
        val extractor: (LangFunctionExtractorEntry) -> SvxExpression?,
        val identifier: String
)