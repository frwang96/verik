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

package verikc.vk.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
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

    companion object {

        operator fun invoke(expression: KtExpressionFunction): VkExpressionFunction {
            val typeSymbol = expression.typeSymbol
                ?: throw LineException("function expression has not been assigned a type", expression.line)
            val functionSymbol = expression.functionSymbol
                ?: throw LineException("function expression has not been resolved", expression.line)

            return VkExpressionFunction(
                expression.line,
                typeSymbol,
                functionSymbol,
                expression.receiver?.let { VkExpression(it) },
                expression.args.map { VkExpression(it) }
            )
        }
    }
}

data class VkExpressionOperator(
    override val line: Line,
    override val typeSymbol: Symbol,
    val operatorSymbol: Symbol,
    val receiver: VkExpression?,
    val args: List<VkExpression>,
    val blocks: List<VkBlock>
): VkExpression(line, typeSymbol) {

    companion object {

        operator fun invoke(expression: KtExpressionOperator): VkExpressionOperator {
            val typeSymbol = expression.typeSymbol
                ?: throw LineException("operator expression has not been assigned a type", expression.line)

            return VkExpressionOperator(
                expression.line,
                typeSymbol,
                expression.operatorSymbol,
                expression.receiver?.let { VkExpression(it) },
                expression.args.map { VkExpression(it) },
                expression.blocks.map { VkBlock(it) }
            )
        }
    }
}

data class VkExpressionProperty(
    override val line: Line,
    override val typeSymbol: Symbol,
    val propertySymbol: Symbol,
    val receiver: VkExpression?
): VkExpression(line, typeSymbol) {

    companion object {

        operator fun invoke(expression: KtExpressionProperty): VkExpressionProperty {
            val typeSymbol = expression.typeSymbol
                ?: throw LineException("property expression has not been assigned a type", expression.line)
            val propertySymbol = expression.propertySymbol
                ?: throw LineException("property expression has not been resolved", expression.line)

            return VkExpressionProperty(
                expression.line,
                typeSymbol,
                propertySymbol,
                expression.receiver?.let { VkExpression(it) }
            )
        }
    }
}

data class VkExpressionString(
    override val line: Line,
    override val typeSymbol: Symbol,
    val segments: List<VkStringSegment>
): VkExpression(line, typeSymbol) {

    companion object {

        operator fun invoke(expression: KtExpressionString): VkExpressionString {
            val typeSymbol = expression.typeSymbol
                ?: throw LineException("string expression has not been assigned a type", expression.line)

            return VkExpressionString(
                expression.line,
                typeSymbol,
                expression.segments.map { VkStringSegment(it) }
            )
        }
    }
}

data class VkExpressionLiteral(
    override val line: Line,
    override val typeSymbol: Symbol,
    val value: LiteralValue
): VkExpression(line, typeSymbol) {

    companion object {

        operator fun invoke(expression: KtExpressionLiteral): VkExpressionLiteral {
            val typeSymbol = expression.typeSymbol
                ?: throw LineException("literal expression has not been assigned a type", expression.line)

            return VkExpressionLiteral(
                expression.line,
                typeSymbol,
                expression.value
            )
        }
    }
}
