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

package verikc.ps.ast

import verikc.base.ast.Symbol
import verikc.ps.extract.PsExpressionExtractorLiteral
import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfEnumEntry
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvEnumEntry

data class PsEnumEntry(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val expression: PsExpressionLiteral
): PsDeclaration {

    fun extract(prefix: String): SvEnumEntry {
        return SvEnumEntry(
                line,
                prefix + identifier.toUpperCase(),
                PsExpressionExtractorLiteral.extract(expression)
        )
    }

    constructor(enumEntry: RfEnumEntry): this(
            enumEntry.line,
            enumEntry.identifier,
            enumEntry.symbol,
            PsExpressionLiteral(enumEntry.expression)
    )
}

data class PsEnum(
        override val line: Int,
        override val identifier: String,
        override val symbol: Symbol,
        val entries: List<PsEnumEntry>,
        val width: Int
): PsDeclaration {

    fun extract(): SvEnum {
        val prefix = identifier.substring(1).toUpperCase() + "_"
        return SvEnum(
                line,
                identifier.substring(1),
                entries.map { it.extract(prefix) },
                width
        )
    }

    constructor(enum: RfEnum): this(
            enum.line,
            enum.identifier,
            enum.symbol,
            enum.entries.map { PsEnumEntry(it) },
            enum.width
    )
}
