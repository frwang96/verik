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

package verik.core.vk.ast

import verik.core.base.Line
import verik.core.kt.ast.KtStringSegment
import verik.core.kt.ast.KtStringSegmentExpression
import verik.core.kt.ast.KtStringSegmentExpressionBase
import verik.core.kt.ast.KtStringSegmentLiteral

enum class VkStringSegmentExpressionBase {
    DEFAULT,
    BIN,
    HEX;

    companion object {

        operator fun invoke(base: KtStringSegmentExpressionBase): VkStringSegmentExpressionBase {
            return when (base) {
                KtStringSegmentExpressionBase.DEFAULT -> DEFAULT
                KtStringSegmentExpressionBase.BIN -> BIN
                KtStringSegmentExpressionBase.HEX -> HEX
            }
        }
    }
}

sealed class VkStringSegment(
        override val line: Int
): Line {

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
        override val line: Int,
        val string: String
): VkStringSegment(line) {

    constructor(segment: KtStringSegmentLiteral): this(
            segment.line,
            segment.string
    )
}

data class VkStringSegmentExpression(
        override val line: Int,
        val base: VkStringSegmentExpressionBase,
        val expression: VkExpression
): VkStringSegment(line) {

    constructor(segment: KtStringSegmentExpression): this(
            segment.line,
            VkStringSegmentExpressionBase(segment.base),
            VkExpression(segment.expression)
    )
}
