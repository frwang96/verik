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

package verik.core.vk

import verik.core.ktx.KtxStringSegment
import verik.core.ktx.KtxStringSegmentExpression
import verik.core.ktx.KtxStringSegmentLiteral
import verik.core.main.Line

sealed class VkStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: KtxStringSegment): VkStringSegment {
            return when (segment) {
                is KtxStringSegmentLiteral -> VkStringSegmentLiteral(segment)
                is KtxStringSegmentExpression -> VkStringSegmentExpression(segment)
            }
        }
    }
}

data class VkStringSegmentLiteral(
        override val line: Int,
        val string: String
): VkStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtxStringSegmentLiteral): VkStringSegmentLiteral {
            return VkStringSegmentLiteral(
                    segment.line,
                    segment.string
            )
        }
    }
}

data class VkStringSegmentExpression(
        override val line: Int,
        val expression: VkExpression
): VkStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtxStringSegmentExpression): VkStringSegmentExpression {
            return VkStringSegmentExpression(
                    segment.line,
                    VkExpression(segment.expression)
            )
        }
    }
}
