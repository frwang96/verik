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

package verikc.kt.ast

import verikc.al.AlRule
import verikc.base.SymbolContext
import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.Symbol
import verikc.kt.parse.KtParserExpression

sealed class KtExpression(
    open val line: Line,
    open var typeSymbol: Symbol?
) {

    companion object {

        operator fun invoke(expression: AlRule, symbolContext: SymbolContext): KtExpression {
            return KtParserExpression.parse(expression, symbolContext)
        }
    }
}

data class KtExpressionFunction(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val identifier: String,
    val receiver: KtExpression?,
    val args: List<KtExpression>,
    var functionSymbol: Symbol?
): KtExpression(line, typeSymbol)

data class KtExpressionOperator(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val operatorSymbol: Symbol,
    val receiver: KtExpression?,
    val args: List<KtExpression>,
    val blocks: List<KtBlock>
): KtExpression(line, typeSymbol)

data class KtExpressionProperty(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val identifier: String,
    val receiver: KtExpression?,
    var propertySymbol: Symbol?
): KtExpression(line, typeSymbol)

data class KtExpressionString(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val segments: List<KtStringSegment>
): KtExpression(line, typeSymbol)

data class KtExpressionLiteral(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val value: LiteralValue
): KtExpression(line, typeSymbol)
