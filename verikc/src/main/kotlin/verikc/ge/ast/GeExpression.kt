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

package verikc.ge.ast

import verikc.base.ast.*
import verikc.base.symbol.Symbol
import verikc.rs.ast.*

sealed class GeExpression(
    open val line: Line,
    open val typeSymbol: Symbol,
    open var typeGenerified: TypeGenerified?,
    open var expressionClass: ExpressionClass?
) {

    companion object {

        operator fun invoke(expression: RsExpression): GeExpression {
            return when (expression) {
                is RsExpressionFunction -> GeExpressionFunction(expression)
                is RsExpressionOperator -> GeExpressionOperator(expression)
                is RsExpressionProperty -> GeExpressionProperty(expression)
                is RsExpressionString -> GeExpressionString(expression)
                is RsExpressionLiteral -> GeExpressionLiteral(expression)
            }
        }
    }

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return typeGenerified
            ?: throw LineException("expression has not been generified", line)
    }

    fun getExpressionClassNotNull(): ExpressionClass {
        return expressionClass
            ?: throw LineException("expression has not been generified", line)
    }
}

data class GeExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val functionSymbol: Symbol,
    val receiver: GeExpression?,
    val args: List<GeExpression>
): GeExpression(line, typeSymbol, typeGenerified, expressionClass) {

    constructor(expression: RsExpressionFunction): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        null,
        expression.getFunctionSymbolNotNull(),
        expression.receiver?.let { GeExpression(it) },
        expression.args.map { GeExpression(it) }
    )
}

data class GeExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val operatorSymbol: Symbol,
    val receiver: GeExpression?,
    val args: List<GeExpression>,
    val blocks: List<GeBlock>
): GeExpression(line, typeSymbol, typeGenerified, expressionClass) {

    constructor(expression: RsExpressionOperator): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        null,
        expression.operatorSymbol,
        expression.receiver?.let { GeExpression(it) },
        expression.args.map { GeExpression(it) },
        expression.blocks.map { GeBlock(it) }
    )
}

data class GeExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val propertySymbol: Symbol,
    val receiver: GeExpression?
): GeExpression(line, typeSymbol, typeGenerified, expressionClass) {

    constructor(expression: RsExpressionProperty): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        null,
        expression.getPropertySymbolNotNull(),
        expression.receiver?.let { GeExpression(it) }
    )
}

data class GeExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val segments: List<GeStringSegment>
): GeExpression(line, typeSymbol, typeGenerified, expressionClass) {

    constructor(expression: RsExpressionString): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        null,
        expression.segments.map { GeStringSegment(it) }
    )
}

data class GeExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val value: LiteralValue
): GeExpression(line, typeSymbol, typeGenerified, expressionClass) {

    constructor(expression: RsExpressionLiteral): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        null,
        null,
        expression.getValueNotNull()
    )
}
