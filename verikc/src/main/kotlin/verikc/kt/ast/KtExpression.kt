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

import verikc.al.ast.AlTree
import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.base.symbol.SymbolContext
import verikc.kt.parse.KtParserExpressionBase

sealed class KtExpression(
    open val line: Line,
    open var typeSymbol: Symbol?
) {

    fun getTypeSymbolNotNull(): Symbol {
        return if (typeSymbol != null) {
            typeSymbol!!
        } else {
            val expressionType = when (this) {
                is KtExpressionFunction -> "function"
                is KtExpressionOperator -> "operator"
                is KtExpressionProperty -> "property"
                is KtExpressionString -> "string"
                is KtExpressionLiteral -> "literal"
            }
            throw LineException("$expressionType expression has not been resolved", line)
        }
    }

    companion object {

        operator fun invoke(expression: AlTree, symbolContext: SymbolContext): KtExpression {
            return KtParserExpressionBase.parse(expression, symbolContext)
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
): KtExpression(line, typeSymbol) {

    fun getFunctionSymbolNotNull(): Symbol {
        return functionSymbol
            ?: throw LineException("function expression has not been resolved", line)
    }
}

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
): KtExpression(line, typeSymbol) {

    fun getPropertySymbolNotNull(): Symbol {
        return propertySymbol
            ?: throw LineException("property expression has not been resolved", line)
    }
}

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
