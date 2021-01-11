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
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.rsx.ast.*
import verikc.vk.build.VkBuilderBlock

sealed class VkExpression(
    open val line: Line,
    open val typeGenerified: TypeGenerified
) {

    companion object {

        operator fun invoke(expression: RsxExpression): VkExpression {
            return when (expression) {
                is RsxExpressionFunction -> VkExpressionFunction(expression)
                is RsxExpressionOperator -> VkExpressionOperator(expression)
                is RsxExpressionProperty -> VkExpressionProperty(expression)
                is RsxExpressionString -> VkExpressionString(expression)
                is RsxExpressionLiteral -> VkExpressionLiteral(expression)
            }
        }
    }
}

data class VkExpressionFunction(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val functionSymbol: Symbol,
    val receiver: VkExpression?,
    val args: List<VkExpression>
): VkExpression(line, typeGenerified) {

    constructor(expression: RsxExpressionFunction): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.getFunctionSymbolNotNull(),
        expression.receiver?.let { VkExpression(it) },
        expression.args.map { VkExpression(it) }
    )
}

data class VkExpressionOperator(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val operatorSymbol: Symbol,
    val receiver: VkExpression?,
    val args: List<VkExpression>,
    val blocks: List<VkBlock>
): VkExpression(line, typeGenerified) {

    constructor(expression: RsxExpressionOperator): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.operatorSymbol,
        expression.receiver?.let { VkExpression(it) },
        expression.args.map { VkExpression(it) },
        expression.blocks.map { VkBuilderBlock.build(it) }
    )
}

data class VkExpressionProperty(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val propertySymbol: Symbol,
    val receiver: VkExpression?
): VkExpression(line, typeGenerified) {

    constructor(expression: RsxExpressionProperty): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.getPropertySymbolNotNull(),
        expression.receiver?.let { VkExpression(it) }
    )
}

data class VkExpressionString(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val segments: List<VkStringSegment>
): VkExpression(line, typeGenerified) {

    constructor(expression: RsxExpressionString): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.segments.map { VkStringSegment(it) }
    )
}

data class VkExpressionLiteral(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val value: LiteralValue
): VkExpression(line, typeGenerified) {

    constructor(expression: RsxExpressionLiteral): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.getValueNotNull()
    )
}
