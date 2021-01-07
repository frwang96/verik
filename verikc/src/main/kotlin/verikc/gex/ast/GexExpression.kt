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

package verikc.gex.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rs.ast.*

sealed class GexExpression(
    open val line: Line,
    open val typeSymbol: Symbol,
    open var typeGenerified: TypeGenerified?
) {

    companion object {

        operator fun invoke(expression: RsExpression): GexExpression {
            return when (expression) {
                is RsExpressionFunction -> GexExpressionFunction(expression)
                is RsExpressionOperator -> GexExpressionOperator(expression)
                is RsExpressionProperty -> GexExpressionProperty(expression)
                is RsExpressionString -> GexExpressionString(expression)
                is RsExpressionLiteral -> GexExpressionLiteral(expression)
            }
        }
    }

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return typeGenerified
            ?: throw LineException("expression has not been generified", line)
    }
}

data class GexExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val functionSymbol: Symbol,
    val receiver: GexExpression?,
    val args: List<GexExpression>
): GexExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: RsExpressionFunction): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        expression.getFunctionSymbolNotNull(),
        expression.receiver?.let { GexExpression(it) },
        expression.args.map { GexExpression(it) }
    )
}

data class GexExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val operatorSymbol: Symbol,
    val receiver: GexExpression?,
    val args: List<GexExpression>,
    val blocks: List<GexBlock>
): GexExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: RsExpressionOperator): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        expression.operatorSymbol,
        expression.receiver?.let { GexExpression(it) },
        expression.args.map { GexExpression(it) },
        expression.blocks.map { GexBlock(it) }
    )
}

data class GexExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val propertySymbol: Symbol,
    val receiver: GexExpression?
): GexExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: RsExpressionProperty): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        expression.getPropertySymbolNotNull(),
        expression.receiver?.let { GexExpression(it) }
    )
}

data class GexExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val segments: List<GexStringSegment>
): GexExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: RsExpressionString): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        expression.segments.map { GexStringSegment(it) }
    )
}

data class GexExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val value: LiteralValue
): GexExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: RsExpressionLiteral): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        expression.getValueNotNull()
    )
}
