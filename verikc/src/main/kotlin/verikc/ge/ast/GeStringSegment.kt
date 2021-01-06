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

package verikc.ge.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.vk.ast.VkStringSegment
import verikc.vk.ast.VkStringSegmentExpression
import verikc.vk.ast.VkStringSegmentLiteral

sealed class GeStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(segment: VkStringSegment): GeStringSegment {
            return when (segment) {
                is VkStringSegmentLiteral -> GeStringSegmentLiteral(segment)
                is VkStringSegmentExpression -> GeStringSegmentExpression(segment)
            }
        }
    }
}

data class GeStringSegmentLiteral(
    override val line: Line,
    val string: String
): GeStringSegment(line) {

    constructor(segment: VkStringSegmentLiteral): this(
        segment.line,
        segment.string
    )
}

data class GeStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: GeExpression
): GeStringSegment(line) {

    constructor(segment: VkStringSegmentExpression): this(
        segment.line,
        segment.baseType,
        GeExpression(segment.expression)
    )
}
