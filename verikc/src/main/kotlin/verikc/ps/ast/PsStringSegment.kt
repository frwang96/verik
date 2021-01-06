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

package verikc.ps.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.ge.ast.GeStringSegment
import verikc.ge.ast.GeStringSegmentExpression
import verikc.ge.ast.GeStringSegmentLiteral

sealed class PsStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(segment: GeStringSegment): PsStringSegment {
            return when (segment) {
                is GeStringSegmentLiteral -> PsStringSegmentLiteral(segment)
                is GeStringSegmentExpression -> PsStringSegmentExpression(segment)
            }
        }
    }
}

data class PsStringSegmentLiteral(
    override val line: Line,
    val string: String
): PsStringSegment(line) {

    constructor(segment: GeStringSegmentLiteral): this(
        segment.line,
        segment.string
    )
}

data class PsStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    var expression: PsExpression
): PsStringSegment(line) {

    constructor(segment: GeStringSegmentExpression): this(
        segment.line,
        segment.baseType,
        PsExpression(segment.expression)
    )
}
