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

package verik.core.kt

import verik.core.al.AlRule
import verik.core.main.Line
import verik.core.symbol.Symbol

sealed class KtExpression(
        override val line: Int,
        open var type: Symbol?
): Line {

    companion object {

        operator fun invoke(expression: AlRule): KtExpression {
            return KtExpressionParser.parse(expression)
        }
    }
}

data class KtExpressionFunction(
        override val line: Int,
        override var type: Symbol?,
        val target: KtExpression?,
        val identifier: String,
        val args: List<KtExpression>,
        var function: Symbol?
): KtExpression(line, type)

data class KtExpressionOperator(
        override val line: Int,
        override var type: Symbol?,
        val target: KtExpression?,
        val identifier: KtOperatorIdentifier,
        val args: List<KtExpression>,
        val blocks: List<KtBlock>
): KtExpression(line, type)

data class KtExpressionProperty(
        override val line: Int,
        override var type: Symbol?,
        val target: KtExpression?,
        val identifier: String,
        var property: Symbol?
): KtExpression(line, type)

data class KtExpressionString(
        override val line: Int,
        override var type: Symbol?,
        val segments: List<KtStringSegment>
): KtExpression(line, type)

data class KtExpressionLiteral(
        override val line: Int,
        override var type: Symbol?,
        val value: String
): KtExpression(line, type)
