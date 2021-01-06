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

package verikc.rs.ast

import verikc.base.ast.BaseType
import verikc.base.ast.Line
import verikc.kt.ast.KtStringSegment
import verikc.kt.ast.KtStringSegmentExpression
import verikc.kt.ast.KtStringSegmentLiteral

sealed class RsStringSegment(
    open val line: Line
) {

    companion object {

        operator fun invoke(stringSegment: KtStringSegment): RsStringSegment {
            return when (stringSegment) {
                is KtStringSegmentLiteral -> RsStringSegmentLiteral(stringSegment)
                is KtStringSegmentExpression -> RsStringSegmentExpression(stringSegment)
            }
        }
    }
}

data class RsStringSegmentLiteral(
    override val line: Line,
    val string: String
): RsStringSegment(line) {

    constructor(stringSegment: KtStringSegmentLiteral): this(
        stringSegment.line,
        stringSegment.string
    )
}

data class RsStringSegmentExpression(
    override val line: Line,
    val baseType: BaseType,
    val expression: RsExpression
): RsStringSegment(line) {

    constructor(stringSegment: KtStringSegmentExpression): this(
        stringSegment.line,
        stringSegment.baseType,
        RsExpression(stringSegment.expression)
    )
}
