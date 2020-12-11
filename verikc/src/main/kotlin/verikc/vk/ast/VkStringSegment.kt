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

package verikc.vk.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.kt.ast.KtStringSegment
import verikc.kt.ast.KtStringSegmentExpression
import verikc.kt.ast.KtStringSegmentLiteral

sealed class VkStringSegment(
        open val line: Line
) {

    companion object {

        operator fun invoke(segment: KtStringSegment): VkStringSegment {
            return when (segment) {
                is KtStringSegmentLiteral -> VkStringSegmentLiteral(segment)
                is KtStringSegmentExpression -> VkStringSegmentExpression(segment)
            }
        }
    }
}

data class VkStringSegmentLiteral(
        override val line: Line,
        val string: String
): VkStringSegment(line) {

    constructor(segment: KtStringSegmentLiteral): this(
            segment.line,
            segment.string
    )
}

data class VkStringSegmentExpression(
        override val line: Line,
        val baseType: BaseType,
        val expression: VkExpression
): VkStringSegment(line) {

    constructor(segment: KtStringSegmentExpression): this(
            segment.line,
            segment.baseType,
            VkExpression(segment.expression)
    )
}
