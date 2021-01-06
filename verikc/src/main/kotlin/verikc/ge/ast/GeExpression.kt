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

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.vk.ast.*

sealed class GeExpression(
    open val line: Line,
    open val typeSymbol: Symbol,
    open var typeGenerified: TypeGenerified?
) {

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return if (typeGenerified != null) {
            typeGenerified!!
        } else {
            val expressionType = when (this) {
                is GeExpressionFunction -> "function"
                is GeExpressionOperator -> "operator"
                is GeExpressionProperty -> "property"
                is GeExpressionString -> "string"
                is GeExpressionLiteral -> "literal"
            }
            throw LineException("$expressionType expression has not been generified", line)
        }
    }

    companion object {

        operator fun invoke(expression: VkExpression): GeExpression {
            return when (expression) {
                is VkExpressionFunction -> GeExpressionFunction(expression)
                is VkExpressionOperator -> GeExpressionOperator(expression)
                is VkExpressionProperty -> GeExpressionProperty(expression)
                is VkExpressionString -> GeExpressionString(expression)
                is VkExpressionLiteral -> GeExpressionLiteral(expression)
            }
        }
    }
}

data class GeExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val functionSymbol: Symbol,
    val receiver: GeExpression?,
    val args: List<GeExpression>
): GeExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: VkExpressionFunction): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.functionSymbol,
        expression.receiver?.let { GeExpression(it) },
        expression.args.map { GeExpression(it) }
    )
}

data class GeExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val operatorSymbol: Symbol,
    val receiver: GeExpression?,
    val args: List<GeExpression>,
    val blocks: List<GeBlock>
): GeExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: VkExpressionOperator): this(
        expression.line,
        expression.typeSymbol,
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
    val propertySymbol: Symbol,
    val receiver: GeExpression?
): GeExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: VkExpressionProperty): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.propertySymbol,
        expression.receiver?.let { GeExpression(it) }
    )
}

data class GeExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val segments: List<GeStringSegment>
): GeExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: VkExpressionString): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.segments.map { GeStringSegment(it) }
    )
}

data class GeExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeGenerified: TypeGenerified?,
    val value: LiteralValue
): GeExpression(line, typeSymbol, typeGenerified) {

    constructor(expression: VkExpressionLiteral): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.value
    )
}
