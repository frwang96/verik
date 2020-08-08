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

package io.verik.core.kt

import io.verik.core.FileLine
import io.verik.core.al.AlRule

sealed class KtExpression(
        open val fileLine: FileLine,
        open var type: KtType?
) {

    companion object {

        operator fun invoke(expression: AlRule): KtExpression {
            return KtExpressionParser.parse(expression)
        }
    }
}

data class KtExpressionFunction(
        override val fileLine: FileLine,
        override var type: KtType?,
        val target: KtExpression?,
        val identifier: KtFunctionIdentifier,
        val args: List<KtExpression>
): KtExpression(fileLine, type) {

    constructor(
            fileLine: FileLine,
            target: KtExpression?,
            identifier: KtFunctionIdentifier,
            args: List<KtExpression>
    ): this(fileLine, null, target, identifier, args)

    constructor(
            target: KtExpression?,
            identifier: KtFunctionIdentifier,
            args: List<KtExpression>
    ): this(FileLine(), null, target, identifier, args)
}

data class KtExpressionIdentifier(
        override val fileLine: FileLine,
        override var type: KtType?,
        val target: KtExpression?,
        val identifier: String
): KtExpression(fileLine, type) {

    constructor(
            fileLine: FileLine,
            target: KtExpression?,
            identifier: String
    ): this(fileLine, null, target, identifier)

    constructor(
            target: KtExpression?,
            identifier: String
    ): this(FileLine(), null, target, identifier)
}

data class KtExpressionLambda(
        override val fileLine: FileLine,
        override var type: KtType?,
        val block: KtBlock
): KtExpression(fileLine, type) {

    constructor(
            fileLine: FileLine,
            block: KtBlock
    ): this(fileLine, null, block)

    constructor(
            block: KtBlock
    ): this(FileLine(), null, block)
}

data class KtExpressionString(
        override val fileLine: FileLine,
        override var type: KtType?,
        val segments: List<KtStringSegment>
): KtExpression(fileLine, type) {

    constructor(
            fileLine: FileLine,
            segments: List<KtStringSegment>
    ): this(fileLine, null, segments)

    constructor(
            segments: List<KtStringSegment>
    ): this(FileLine(), null, segments)
}

data class KtExpressionLiteral(
        override val fileLine: FileLine,
        override var type: KtType?,
        val value: String
): KtExpression(fileLine, type) {

    constructor(
            fileLine: FileLine,
            value: String
    ): this(fileLine, null, value)


    constructor(
            value: String
    ): this(FileLine(), null, value)
}
