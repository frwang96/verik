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

package verikc.rf.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.vk.ast.*

sealed class RfExpression(
    open val line: Line,
    open val typeSymbol: Symbol,
    open var typeReified: TypeReified?
) {

    fun getTypeReifiedNotNull(): TypeReified {
        return if (typeReified != null) {
            typeReified!!
        } else {
            val expressionType = when (this) {
                is RfExpressionFunction -> "function"
                is RfExpressionOperator -> "operator"
                is RfExpressionProperty -> "property"
                is RfExpressionString -> "string"
                is RfExpressionLiteral -> "literal"
            }
            throw LineException("$expressionType expression has not been reified", line)
        }
    }

    companion object {

        operator fun invoke(expression: VkExpression): RfExpression {
            return when (expression) {
                is VkExpressionFunction -> RfExpressionFunction(expression)
                is VkExpressionOperator -> RfExpressionOperator(expression)
                is VkExpressionProperty -> RfExpressionProperty(expression)
                is VkExpressionString -> RfExpressionString(expression)
                is VkExpressionLiteral -> RfExpressionLiteral(expression)
            }
        }
    }
}

data class RfExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val functionSymbol: Symbol,
    val receiver: RfExpression?,
    val args: List<RfExpression>
): RfExpression(line, typeSymbol, typeReified) {

    constructor(expression: VkExpressionFunction): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.functionSymbol,
        expression.receiver?.let { RfExpression(it) },
        expression.args.map { RfExpression(it) }
    )
}

data class RfExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val operatorSymbol: Symbol,
    val receiver: RfExpression?,
    val args: List<RfExpression>,
    val blocks: List<RfBlock>
): RfExpression(line, typeSymbol, typeReified) {

    constructor(expression: VkExpressionOperator): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.operatorSymbol,
        expression.receiver?.let { RfExpression(it) },
        expression.args.map { RfExpression(it) },
        expression.blocks.map { RfBlock(it) }
    )
}

data class RfExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val propertySymbol: Symbol,
    val receiver: RfExpression?
): RfExpression(line, typeSymbol, typeReified) {

    constructor(expression: VkExpressionProperty): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.propertySymbol,
        expression.receiver?.let { RfExpression(it) }
    )
}

data class RfExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val segments: List<RfStringSegment>
): RfExpression(line, typeSymbol, typeReified) {

    constructor(expression: VkExpressionString): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.segments.map { RfStringSegment(it) }
    )
}

data class RfExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    override var typeReified: TypeReified?,
    val value: LiteralValue
): RfExpression(line, typeSymbol, typeReified) {

    constructor(expression: VkExpressionLiteral): this(
        expression.line,
        expression.typeSymbol,
        null,
        expression.value
    )
}
