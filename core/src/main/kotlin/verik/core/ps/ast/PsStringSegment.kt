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

import verik.core.base.Line
import verik.core.rf.ast.RfStringSegment
import verik.core.rf.ast.RfStringSegmentExpression
import verik.core.rf.ast.RfStringSegmentExpressionBase
import verik.core.rf.ast.RfStringSegmentLiteral

enum class PsStringSegmentExpressionBase {
    DEFAULT,
    BIN,
    HEX;

    companion object {

        operator fun invoke(base: RfStringSegmentExpressionBase): PsStringSegmentExpressionBase {
            return when (base) {
                RfStringSegmentExpressionBase.DEFAULT -> DEFAULT
                RfStringSegmentExpressionBase.BIN -> BIN
                RfStringSegmentExpressionBase.HEX -> HEX
            }
        }
    }
}

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
        val base: PsStringSegmentExpressionBase,
        var expression: PsExpression
): PsStringSegment(line) {

    constructor(segment: RfStringSegmentExpression): this(
            segment.line,
            PsStringSegmentExpressionBase(segment.base),
            PsExpression(segment.expression)
    )
}