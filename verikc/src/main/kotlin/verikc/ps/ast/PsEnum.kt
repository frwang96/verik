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
import verikc.base.ast.LineException
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.rf.ast.RfEnum
import verikc.rf.ast.RfEnumProperty

data class PsEnum(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    val properties: List<PsEnumProperty>,
    val width: Int
): PsDeclaration {

    constructor(enum: RfEnum): this(
        enum.line,
        enum.identifier,
        enum.symbol,
        enum.properties.map { PsEnumProperty(it) },
        enum.width
    )
}

data class PsEnumProperty(
    override val line: Line,
    override val identifier: String,
    override val symbol: Symbol,
    override val typeReified: TypeReified,
    val expression: PsExpressionLiteral
): PsProperty {

    companion object {

        operator fun invoke(enumProperty: RfEnumProperty): PsEnumProperty {
            val typeReified = enumProperty.typeReified
                ?: throw LineException("property ${enumProperty.symbol} has not been reified", enumProperty.line)

            return PsEnumProperty(
                enumProperty.line,
                enumProperty.identifier,
                enumProperty.symbol,
                typeReified,
                PsExpressionLiteral(enumProperty.expression)
            )
        }
    }
}
