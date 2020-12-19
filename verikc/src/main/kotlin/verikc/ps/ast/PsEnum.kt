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

import verikc.base.ast.Line
import verikc.base.ast.Symbol
import verikc.ps.extract.PsExpressionExtractorLiteral
import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfEnumProperty
import verikc.sv.ast.SvEnum
import verikc.sv.ast.SvEnumProperty

data class PsEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val expression: PsExpressionLiteral
): PsDeclaration {

    fun extract(prefix: String): SvEnumProperty {
        return SvEnumProperty(
            line,
            prefix + identifier.toUpperCase(),
            PsExpressionExtractorLiteral.extract(expression)
        )
    }

    constructor(enumProperty: RfEnumProperty): this(
        enumProperty.line,
        enumProperty.identifier,
        enumProperty.symbol,
        PsExpressionLiteral(enumProperty.expression)
    )
}

data class PsEnum(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val properties: List<PsEnumProperty>,
    val width: Int
): PsDeclaration {

    fun extract(): SvEnum {
        val prefix = identifier.substring(1).toUpperCase() + "_"
        return SvEnum(
            line,
            identifier.substring(1),
            properties.map { it.extract(prefix) },
            width
        )
    }

    constructor(enum: RfEnum): this(
        enum.line,
        enum.identifier,
        enum.symbol,
        enum.properties.map { PsEnumProperty(it) },
        enum.width
    )
}
