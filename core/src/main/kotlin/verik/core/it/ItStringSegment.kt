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

package verik.core.it

import verik.core.main.Line
import verik.core.vk.VkStringSegment
import verik.core.vk.VkStringSegmentExpression
import verik.core.vk.VkStringSegmentExpressionBase
import verik.core.vk.VkStringSegmentLiteral

enum class ItStringSegmentExpressionBase {
    BIN,
    DEC,
    HEX;

    companion object {

        operator fun invoke(base: VkStringSegmentExpressionBase): ItStringSegmentExpressionBase {
            return when (base) {
                VkStringSegmentExpressionBase.BIN -> BIN
                VkStringSegmentExpressionBase.DEC -> DEC
                VkStringSegmentExpressionBase.HEX -> HEX
            }
        }
    }
}

sealed class ItStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: VkStringSegment): ItStringSegment {
            return when (segment) {
                is VkStringSegmentLiteral -> ItStringSegmentLiteral(segment)
                is VkStringSegmentExpression -> ItStringSegmentExpression(segment)
            }
        }
    }
}

data class ItStringSegmentLiteral(
        override val line: Int,
        val string: String
): ItStringSegment(line) {

    constructor(segment: VkStringSegmentLiteral): this(
            segment.line,
            segment.string
    )
}

data class ItStringSegmentExpression(
        override val line: Int,
        val base: ItStringSegmentExpressionBase,
        val expression: ItExpression
): ItStringSegment(line) {

    constructor(segment: VkStringSegmentExpression): this(
            segment.line,
            ItStringSegmentExpressionBase(segment.base),
            ItExpression(segment.expression)
    )
}