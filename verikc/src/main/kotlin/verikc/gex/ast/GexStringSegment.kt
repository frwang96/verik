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

package verikc.gex.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.rs.ast.RsStringSegment
import verikc.rs.ast.RsStringSegmentExpression
import verikc.rs.ast.RsStringSegmentLiteral

sealed class GexStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(stringSegment: RsStringSegment): GexStringSegment {
            return when (stringSegment) {
                is RsStringSegmentLiteral -> GexStringSegmentLiteral(stringSegment)
                is RsStringSegmentExpression -> GexStringSegmentExpression(stringSegment)
            }
        }
    }
}

data class GexStringSegmentLiteral(
    override val line: Line,
    val string: String
): GexStringSegment(line) {

    constructor(stringSegment: RsStringSegmentLiteral): this(
        stringSegment.line,
        stringSegment.string
    )
}

data class GexStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: GexExpression
): GexStringSegment(line) {

    constructor(stringSegment: RsStringSegmentExpression): this(
        stringSegment.line,
        stringSegment.baseType,
        GexExpression(stringSegment.expression)
    )
}
