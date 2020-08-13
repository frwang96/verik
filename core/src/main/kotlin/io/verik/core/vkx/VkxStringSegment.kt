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

package io.verik.core.vkx

import io.verik.core.kt.KtStringSegment
import io.verik.core.kt.KtStringSegmentExpression
import io.verik.core.kt.KtStringSegmentLiteral
import io.verik.core.main.Line

sealed class VkxStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: KtStringSegment): VkxStringSegment {
            return when (segment) {
                is KtStringSegmentLiteral -> VkxStringSegmentLiteral(segment)
                is KtStringSegmentExpression -> VkxStringSegmentExpression(segment)
            }
        }
    }
}

data class VkxStringSegmentLiteral(
        override val line: Int,
        val string: String
): VkxStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtStringSegmentLiteral): VkxStringSegmentLiteral {
            return VkxStringSegmentLiteral(
                    segment.line,
                    segment.string
            )
        }
    }
}

data class VkxStringSegmentExpression(
        override val line: Int,
        val expression: VkxExpression
): VkxStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtStringSegmentExpression): VkxStringSegmentExpression {
            return VkxStringSegmentExpression(
                    segment.line,
                    VkxExpression(segment.expression)
            )
        }
    }
}
