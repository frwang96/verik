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

package verikc.kt.ast

import verikc.al.ast.AlTree
import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.parse.KtParserExpressionBase

sealed class KtExpression(
    open val line: Line
) {

    companion object {

        operator fun invoke(expression: AlTree, symbolContext: SymbolContext): KtExpression {
            return KtParserExpressionBase.parse(expression, symbolContext)
        }
    }
}

data class KtExpressionFunction(
    override val line: Line,
    val identifier: String,
    val receiver: KtExpression?,
    val argIdentifiers: List<String>?,
    val args: List<KtExpression>
): KtExpression(line)

data class KtExpressionOperator(
    override val line: Line,
    val operatorSymbol: Symbol,
    val receiver: KtExpression?,
    val args: List<KtExpression>,
    val blocks: List<KtBlock>
): KtExpression(line)

data class KtExpressionProperty(
    override val line: Line,
    val identifier: String,
    val receiver: KtExpression?
): KtExpression(line)

data class KtExpressionString(
    override val line: Line,
    val segments: List<KtStringSegment>
): KtExpression(line)

data class KtExpressionLiteral(
    override val line: Line,
    val string: String
): KtExpression(line)
