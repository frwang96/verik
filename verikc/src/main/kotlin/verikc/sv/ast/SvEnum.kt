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

package verikc.sv.ast

import verikc.base.ast.Line
import verikc.lang.util.LangIdentifierUtil
import verikc.ps.ast.PsEnum
import verikc.ps.ast.PsEnumEntry
import verikc.sv.extract.SvExtractorExpressionLiteral

data class SvEnum(
    val line: Line,
    val identifier: String,
    val entries: List<SvEnumEntry>,
    val width: Int
) {

    constructor(enum: PsEnum): this(
        enum.line,
        enum.identifier,
        enum.entries.map { SvEnumEntry(it, enum.identifier) },
        enum.width
    )
}

data class SvEnumEntry(
    val line: Line,
    val identifier: String,
    val expression: SvExpressionLiteral
) {

    constructor(enumEntry: PsEnumEntry, enumIdentifier: String): this(
        enumEntry.property.line,
        LangIdentifierUtil.enumPropertyIdentifier(enumIdentifier, enumEntry.property.identifier),
        SvExtractorExpressionLiteral.extract(enumEntry.expression)
    )
}
