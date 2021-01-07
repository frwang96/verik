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
import verikc.rs.ast.RsStringSegment
import verikc.rs.ast.RsStringSegmentExpression
import verikc.rs.ast.RsStringSegmentLiteral

sealed class VkxStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(segment: RsStringSegment): VkxStringSegment {
            return when (segment) {
                is RsStringSegmentLiteral -> VkxStringSegmentLiteral(segment)
                is RsStringSegmentExpression -> VkxStringSegmentExpression(segment)
            }
        }
    }
}

data class VkxStringSegmentLiteral(
    override val line: Line,
    val string: String
): VkxStringSegment(line) {

    constructor(segment: RsStringSegmentLiteral): this(
        segment.line,
        segment.string
    )
}

data class VkxStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: VkxExpression
): VkxStringSegment(line) {

    constructor(segment: RsStringSegmentExpression): this(
        segment.line,
        segment.baseType,
        VkxExpression(segment.expression)
    )
}
