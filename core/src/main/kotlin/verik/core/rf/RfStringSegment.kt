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

package verik.core.rf

import verik.core.base.Line
import verik.core.vk.VkStringSegment
import verik.core.vk.VkStringSegmentExpression
import verik.core.vk.VkStringSegmentExpressionBase
import verik.core.vk.VkStringSegmentLiteral

enum class RfStringSegmentExpressionBase {
    DEFAULT,
    BIN,
    HEX;

    companion object {

        operator fun invoke(base: VkStringSegmentExpressionBase): RfStringSegmentExpressionBase {
            return when (base) {
                VkStringSegmentExpressionBase.DEFAULT -> DEFAULT
                VkStringSegmentExpressionBase.BIN -> BIN
                VkStringSegmentExpressionBase.HEX -> HEX
            }
        }
    }
}

sealed class RfStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: VkStringSegment): RfStringSegment {
            return when (segment) {
                is VkStringSegmentLiteral -> RfStringSegmentLiteral(segment)
                is VkStringSegmentExpression -> RfStringSegmentExpression(segment)
            }
        }
    }
}

data class RfStringSegmentLiteral(
        override val line: Int,
        val string: String
): RfStringSegment(line) {

    constructor(segment: VkStringSegmentLiteral): this(
            segment.line,
            segment.string
    )
}

data class RfStringSegmentExpression(
        override val line: Int,
        val base: RfStringSegmentExpressionBase,
        val expression: RfExpression
): RfStringSegment(line) {

    constructor(segment: VkStringSegmentExpression): this(
            segment.line,
            RfStringSegmentExpressionBase(segment.base),
            RfExpression(segment.expression)
    )
}