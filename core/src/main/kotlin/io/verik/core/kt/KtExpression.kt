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
        open var type: KtType?,
        open val fileLine: FileLine
) {

    companion object {

        operator fun invoke(expression: AlRule): KtExpression {
            return KtExpressionParser.parse(expression)
        }
    }
}

data class KtExpressionFunction(
    override var type: KtType?,
    override val fileLine: FileLine,
    val target: KtExpression?,
    val identifier: KtFunctionIdentifier,
    val args: List<KtExpression>
): KtExpression(type, fileLine) {

    constructor(
            fileLine: FileLine,
            target: KtExpression?,
            identifier: KtFunctionIdentifier,
            args: List<KtExpression>
    ): this(null, fileLine, target, identifier, args)

    constructor(
            target: KtExpression?,
            identifier: KtFunctionIdentifier,
            args: List<KtExpression>
    ): this(null, FileLine(), target, identifier, args)
}

data class KtExpressionIdentifier(
    override var type: KtType?,
    override val fileLine: FileLine,
    val target: KtExpression?,
    val identifier: String
): KtExpression(type, fileLine) {

    constructor(
            fileLine: FileLine,
            target: KtExpression?,
            identifier: String
    ): this(null, fileLine, target, identifier)

    constructor(
            target: KtExpression?,
            identifier: String
    ): this(null, FileLine(), target, identifier)
}

data class KtExpressionLambda(
        override var type: KtType?,
        override val fileLine: FileLine,
        val statements: List<KtStatement>
): KtExpression(type, fileLine) {

    constructor(
            fileLine: FileLine,
            statements: List<KtStatement>
    ): this(null, fileLine, statements)

    constructor(
            statements: List<KtStatement>
    ): this(null, FileLine(), statements)
}

data class KtExpressionString(
    override var type: KtType?,
    override val fileLine: FileLine,
    val segments: List<KtStringSegment>
): KtExpression(type, fileLine) {

    constructor(
            fileLine: FileLine,
            segments: List<KtStringSegment>
    ): this(null, fileLine, segments)

    constructor(
            segments: List<KtStringSegment>
    ): this(null, FileLine(), segments)
}

data class KtExpressionLiteral(
        override var type: KtType?,
        override val fileLine: FileLine,
        val value: String
): KtExpression(type, fileLine) {

    constructor(
            fileLine: FileLine,
            value: String
    ): this(null, fileLine, value)


    constructor(
            value: String
    ): this(null, FileLine(), value)
}
