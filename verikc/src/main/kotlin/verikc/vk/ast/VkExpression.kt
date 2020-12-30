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

package verikc.vk.ast

import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.symbol.Symbol
import verikc.kt.ast.*

sealed class VkExpression(
    open val line: Line,
    open val typeSymbol: Symbol
) {

    companion object {

        operator fun invoke(expression: KtExpression): VkExpression {
            return when (expression) {
                is KtExpressionFunction -> VkExpressionFunction(expression)
                is KtExpressionOperator -> VkExpressionOperator(expression)
                is KtExpressionProperty -> VkExpressionProperty(expression)
                is KtExpressionString -> VkExpressionString(expression)
                is KtExpressionLiteral -> VkExpressionLiteral(expression)
            }
        }
    }
}

data class VkExpressionFunction(
    override val line: Line,
    override val typeSymbol: Symbol,
    val functionSymbol: Symbol,
    val receiver: VkExpression?,
    val args: List<VkExpression>
): VkExpression(line, typeSymbol) {

    constructor(expression: KtExpressionFunction): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        expression.getFunctionSymbolNotNull(),
        expression.receiver?.let { VkExpression(it) },
        expression.args.map { VkExpression(it) }
    )
}

data class VkExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    val operatorSymbol: Symbol,
    val receiver: VkExpression?,
    val args: List<VkExpression>,
    val blocks: List<VkBlock>
): VkExpression(line, typeSymbol) {

    constructor(expression: KtExpressionOperator): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        expression.operatorSymbol,
        expression.receiver?.let { VkExpression(it) },
        expression.args.map { VkExpression(it) },
        expression.blocks.map { VkBlock(it) }
    )
}

data class VkExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    val propertySymbol: Symbol,
    val receiver: VkExpression?
): VkExpression(line, typeSymbol) {

    constructor(expression: KtExpressionProperty): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        expression.getPropertySymbolNotNull(),
        expression.receiver?.let { VkExpression(it) }
    )
}

data class VkExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    val segments: List<VkStringSegment>
): VkExpression(line, typeSymbol) {

    constructor(expression: KtExpressionString): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        expression.segments.map { VkStringSegment(it) }
    )
}

data class VkExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    val value: LiteralValue
): VkExpression(line, typeSymbol) {

    constructor(expression: KtExpressionLiteral): this(
        expression.line,
        expression.getTypeSymbolNotNull(),
        expression.value
    )
}
