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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.ast.*

sealed class RsExpression(
    open val line: Line,
    open var typeSymbol: Symbol?
) {

    fun getTypeSymbolNotNull(): Symbol {
        return if (typeSymbol != null) {
            typeSymbol!!
        } else {
            val expressionType = when (this) {
                is RsExpressionFunction -> "function"
                is RsExpressionOperator -> "operator"
                is RsExpressionProperty -> "property"
                is RsExpressionString -> "string"
                is RsExpressionLiteral -> "literal"
            }
            throw LineException("$expressionType expression has not been resolved", line)
        }
    }

    companion object {

        operator fun invoke(expression: KtExpression): RsExpression {
            return when (expression) {
                is KtExpressionFunction -> RsExpressionFunction(expression)
                is KtExpressionOperator -> RsExpressionOperator(expression)
                is KtExpressionProperty -> RsExpressionProperty(expression)
                is KtExpressionString -> RsExpressionString(expression)
                is KtExpressionLiteral -> RsExpressionLiteral(expression)
            }
        }
    }
}

data class RsExpressionFunction(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val identifier: String,
    val receiver: RsExpression?,
    val args: List<RsExpression>,
    var functionSymbol: Symbol?
): RsExpression(line, typeSymbol) {

    constructor(expression: KtExpressionFunction): this(
        expression.line,
        expression.typeSymbol,
        expression.identifier,
        expression.receiver?.let { RsExpression(it) },
        expression.args.map { RsExpression(it) },
        expression.functionSymbol
    )

    fun getFunctionSymbolNotNull(): Symbol {
        return functionSymbol
            ?: throw LineException("function expression has not been resolved", line)
    }
}

data class RsExpressionOperator(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val operatorSymbol: Symbol,
    val receiver: RsExpression?,
    val args: List<RsExpression>,
    val blocks: List<RsBlock>
): RsExpression(line, typeSymbol) {

    constructor(expression: KtExpressionOperator): this(
        expression.line,
        expression.typeSymbol,
        expression.operatorSymbol,
        expression.receiver?.let { RsExpression(it) },
        expression.args.map { RsExpression(it) },
        expression.blocks.map { RsBlock(it) }
    )
}

data class RsExpressionProperty(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val identifier: String,
    val receiver: RsExpression?,
    var propertySymbol: Symbol?
): RsExpression(line, typeSymbol) {

    constructor(expression: KtExpressionProperty): this(
        expression.line,
        expression.typeSymbol,
        expression.identifier,
        expression.receiver?.let { RsExpression(it) },
        expression.propertySymbol
    )

    fun getPropertySymbolNotNull(): Symbol {
        return propertySymbol
            ?: throw LineException("property expression has not been resolved", line)
    }
}

data class RsExpressionString(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val segments: List<RsStringSegment>
): RsExpression(line, typeSymbol) {

    constructor(expression: KtExpressionString): this(
        expression.line,
        expression.typeSymbol,
        expression.segments.map { RsStringSegment(it) }
    )
}

data class RsExpressionLiteral(
    override val line: Line,
    override var typeSymbol: Symbol?,
    val value: LiteralValue
): RsExpression(line, typeSymbol) {

    constructor(expression: KtExpressionLiteral): this(
        expression.line,
        expression.typeSymbol,
        expression.value
    )
}
