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

package verikc.vkx.ast

import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.gex.ast.*

sealed class VkxExpression(
    open val line: Line,
    open val typeSymbol: Symbol
) {

    companion object {

        operator fun invoke(expression: GexExpression): VkxExpression {
            return when (expression) {
                is GexExpressionFunction -> VkxExpressionFunction(expression)
                is GexExpressionOperator -> VkxExpressionOperator(expression)
                is GexExpressionProperty -> VkxExpressionProperty(expression)
                is GexExpressionString -> VkxExpressionString(expression)
                is GexExpressionLiteral -> VkxExpressionLiteral(expression)
            }
        }
    }
}

data class VkxExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    val functionSymbol: Symbol,
    val receiver: VkxExpression?,
    val args: List<VkxExpression>
): VkxExpression(line, typeSymbol) {

    constructor(expression: GexExpressionFunction): this(
        expression.line,
        expression.typeSymbol,
        expression.functionSymbol,
        expression.receiver?.let { VkxExpression(it) },
        expression.args.map { VkxExpression(it) }
    )
}

data class VkxExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    val operatorSymbol: Symbol,
    val receiver: VkxExpression?,
    val args: List<VkxExpression>,
    val blocks: List<VkxBlock>
): VkxExpression(line, typeSymbol) {

    constructor(expression: GexExpressionOperator): this(
        expression.line,
        expression.typeSymbol,
        expression.operatorSymbol,
        expression.receiver?.let { VkxExpression(it) },
        expression.args.map { VkxExpression(it) },
        expression.blocks.map { VkxBlock(it) }
    )
}

data class VkxExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    val propertySymbol: Symbol,
    val receiver: VkxExpression?
): VkxExpression(line, typeSymbol) {

    constructor(expression: GexExpressionProperty): this(
        expression.line,
        expression.typeSymbol,
        expression.propertySymbol,
        expression.receiver?.let { VkxExpression(it) }
    )
}

data class VkxExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    val segments: List<VkxStringSegment>
): VkxExpression(line, typeSymbol) {

    constructor(expression: GexExpressionString): this(
        expression.line,
        expression.typeSymbol,
        expression.segments.map { VkxStringSegment(it) }
    )
}

data class VkxExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    val value: LiteralValue
): VkxExpression(line, typeSymbol) {

    constructor(expression: GexExpressionLiteral): this(
        expression.line,
        expression.typeSymbol,
        expression.value
    )
}
