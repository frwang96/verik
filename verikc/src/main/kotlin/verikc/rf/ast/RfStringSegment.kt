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

package verikc.rf.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.vk.ast.VkStringSegment
import verikc.vk.ast.VkStringSegmentExpression
import verikc.vk.ast.VkStringSegmentLiteral

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
        val baseType: BaseType,
        val expression: RfExpression
): RfStringSegment(line) {

    constructor(segment: VkStringSegmentExpression): this(
            segment.line,
            segment.baseType,
            RfExpression(segment.expression)
    )
}