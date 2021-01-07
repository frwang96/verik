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

package verikc.vkx.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.gex.ast.GexStringSegment
import verikc.gex.ast.GexStringSegmentExpression
import verikc.gex.ast.GexStringSegmentLiteral

sealed class VkxStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(segment: GexStringSegment): VkxStringSegment {
            return when (segment) {
                is GexStringSegmentLiteral -> VkxStringSegmentLiteral(segment)
                is GexStringSegmentExpression -> VkxStringSegmentExpression(segment)
            }
        }
    }
}

data class VkxStringSegmentLiteral(
    override val line: Line,
    val string: String
): VkxStringSegment(line) {

    constructor(segment: GexStringSegmentLiteral): this(
        segment.line,
        segment.string
    )
}

data class VkxStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: VkxExpression
): VkxStringSegment(line) {

    constructor(segment: GexStringSegmentExpression): this(
        segment.line,
        segment.baseType,
        VkxExpression(segment.expression)
    )
}
