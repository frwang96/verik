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

package verik.core.ps.ast

import verik.core.base.ast.BaseType
import verik.core.base.ast.Line
import verik.core.rf.ast.RfStringSegment
import verik.core.rf.ast.RfStringSegmentExpression
import verik.core.rf.ast.RfStringSegmentLiteral

sealed class PsStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: RfStringSegment): PsStringSegment {
            return when (segment) {
                is RfStringSegmentLiteral -> PsStringSegmentLiteral(segment)
                is RfStringSegmentExpression -> PsStringSegmentExpression(segment)
            }
        }
    }
}

data class PsStringSegmentLiteral(
        override val line: Int,
        val string: String
): PsStringSegment(line) {

    constructor(segment: RfStringSegmentLiteral): this(
            segment.line,
            segment.string
    )
}

data class PsStringSegmentExpression(
        override val line: Int,
        val baseType: BaseType,
        var expression: PsExpression
): PsStringSegment(line) {

    constructor(segment: RfStringSegmentExpression): this(
            segment.line,
            segment.baseType,
            PsExpression(segment.expression)
    )
}