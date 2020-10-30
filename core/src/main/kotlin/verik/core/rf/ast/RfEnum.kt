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

package verik.core.rf.ast

import verik.core.base.Symbol
import verik.core.rf.extract.RfExpressionExtractorLiteral
import verik.core.sv.ast.SvEnum
import verik.core.sv.ast.SvEnumEntry
import verik.core.vk.ast.VkEnum
import verik.core.vk.ast.VkEnumEntry

data class RfEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val expression: RfExpressionLiteral
): RfDeclaration {

    fun extract(prefix: String): SvEnumEntry {
        return SvEnumEntry(
                line,
                prefix + identifier.toUpperCase(),
                RfExpressionExtractorLiteral.extract(expression)
        )
    }

    constructor(enumEntry: VkEnumEntry): this(
            enumEntry.line,
            enumEntry.identifier,
            enumEntry.symbol,
            RfExpressionLiteral(enumEntry.expression)
    )
}

data class RfEnum(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val entries: List<RfEnumEntry>,
        val width: Int
): RfDeclaration {

    fun extract(): SvEnum {
        val prefix = identifier.substring(1).toUpperCase() + "_"
        return SvEnum(
                line,
                identifier.substring(1),
                entries.map { it.extract(prefix) },
                width
        )
    }

    constructor(enum: VkEnum): this(
            enum.line,
            enum.identifier,
            enum.symbol,
            enum.entries.map { RfEnumEntry(it) },
            enum.width
    )
}