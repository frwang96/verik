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

import io.verik.core.Line
import io.verik.core.al.AlRule
import io.verik.core.kt.symbol.KtSymbolFunction
import io.verik.core.kt.symbol.KtSymbolProperty
import io.verik.core.kt.symbol.KtSymbolType

sealed class KtExpression(
        override val line: Int,
        open var type: KtSymbolType?
): Line {

    companion object {

        operator fun invoke(expression: AlRule): KtExpression {
            return KtExpressionParser.parse(expression)
        }
    }
}

data class KtExpressionFunction(
        override val line: Int,
        override var type: KtSymbolType?,
        val target: KtExpression?,
        val identifier: KtFunctionIdentifier,
        val args: List<KtExpression>,
        var function: KtSymbolFunction?
): KtExpression(line, type) {

    constructor(
            line: Int,
            target: KtExpression?,
            identifier: KtFunctionIdentifier,
            args: List<KtExpression>
    ): this(line, null, target, identifier, args, null)
}

data class KtExpressionProperty(
        override val line: Int,
        override var type: KtSymbolType?,
        val target: KtExpression?,
        val identifier: String,
        var property: KtSymbolProperty?
): KtExpression(line, type) {

    constructor(
            line: Int,
            target: KtExpression?,
            identifier: String
    ): this(line, null, target, identifier, null)
}

data class KtExpressionLambda(
        override val line: Int,
        override var type: KtSymbolType?,
        val block: KtBlock
): KtExpression(line, type) {

    constructor(
            line: Int,
            block: KtBlock
    ): this(line, null, block)
}

data class KtExpressionString(
        override val line: Int,
        override var type: KtSymbolType?,
        val segments: List<KtStringSegment>
): KtExpression(line, type) {

    constructor(
            line: Int,
            segments: List<KtStringSegment>
    ): this(line, null, segments)
}

data class KtExpressionLiteral(
        override val line: Int,
        override var type: KtSymbolType?,
        val value: String
): KtExpression(line, type) {

    constructor(
            line: Int,
            value: String
    ): this(line, null, value)
}
