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

package verikc.vk.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.ge.ast.GeStringSegment
import verikc.ge.ast.GeStringSegmentExpression
import verikc.ge.ast.GeStringSegmentLiteral

sealed class VkStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(segment: GeStringSegment): VkStringSegment {
            return when (segment) {
                is GeStringSegmentLiteral -> VkStringSegmentLiteral(segment)
                is GeStringSegmentExpression -> VkStringSegmentExpression(segment)
            }
        }
    }
}

data class VkStringSegmentLiteral(
    override val line: Line,
    val string: String
): VkStringSegment(line) {

    constructor(segment: GeStringSegmentLiteral): this(
        segment.line,
        segment.string
    )
}

data class VkStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: VkExpression
): VkStringSegment(line) {

    constructor(segment: GeStringSegmentExpression): this(
        segment.line,
        segment.baseType,
        VkExpression(segment.expression)
    )
}
