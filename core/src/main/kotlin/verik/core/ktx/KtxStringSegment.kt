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

package verik.core.ktx

import verik.core.kt.KtStringSegment
import verik.core.kt.KtStringSegmentExpression
import verik.core.kt.KtStringSegmentLiteral
import verik.core.main.Line

sealed class KtxStringSegment(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(segment: KtStringSegment): KtxStringSegment {
            return when (segment) {
                is KtStringSegmentLiteral -> KtxStringSegmentLiteral(segment)
                is KtStringSegmentExpression -> KtxStringSegmentExpression(segment)
            }
        }
    }
}

data class KtxStringSegmentLiteral(
        override val line: Int,
        val string: String
): KtxStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtStringSegmentLiteral): KtxStringSegmentLiteral {
            return KtxStringSegmentLiteral(
                    segment.line,
                    segment.string
            )
        }
    }
}

data class KtxStringSegmentExpression(
        override val line: Int,
        val expression: KtxExpression
): KtxStringSegment(line) {

    companion object {

        operator fun invoke(segment: KtStringSegmentExpression): KtxStringSegmentExpression {
            return KtxStringSegmentExpression(
                    segment.line,
                    KtxExpression(segment.expression)
            )
        }
    }
}