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

package verik.core.vk

import verik.core.kt.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

sealed class VkExpression(
        override val line: Int,
        open val type: Symbol
): Line {

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
        override val line: Int,
        override val type: Symbol,
        val target: VkExpression?,
        val args: List<VkExpression>,
        val function: Symbol
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionFunction): VkExpressionFunction {
            val type = expression.type
                    ?: throw LineException("function expression has not been assigned a type", expression)
            val function = expression.function
                    ?: throw LineException("function has not been resolved", expression)

            return VkExpressionFunction(
                    expression.line,
                    type,
                    expression.target?.let { VkExpression(it) },
                    expression.args.map { VkExpression(it) },
                    function
            )
        }
    }
}

data class VkExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        val target: VkExpression?,
        val identifier: VkOperatorIdentifier,
        val args: List<VkExpression>,
        val blocks: List<VkBlock>
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionOperator): VkExpressionOperator {
            val type = expression.type
                    ?: throw LineException("operator expression has not been assigned a type", expression)

            return VkExpressionOperator(
                    expression.line,
                    type,
                    expression.target?.let { VkExpression(it) },
                    VkOperatorIdentifier(expression.identifier, expression.line),
                    expression.args.map { VkExpression(it) },
                    expression.blocks.map { VkBlock(it) }
            )
        }
    }
}

data class VkExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        val target: VkExpression?,
        val property: Symbol
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionProperty): VkExpressionProperty {
            val type = expression.type
                    ?: throw LineException("property expression has not been assigned a type", expression)
            val property = expression.property
                    ?: throw LineException("property expression has not been resolved", expression)

            return VkExpressionProperty(
                    expression.line,
                    type,
                    expression.target?.let { VkExpression(it) },
                    property
            )
        }
    }
}

data class VkExpressionString(
        override val line: Int,
        override val type: Symbol,
        val segments: List<VkStringSegment>
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionString): VkExpressionString {
            val type = expression.type
                    ?: throw LineException("string expression has not been assigned a type", expression)

            return VkExpressionString(
                    expression.line,
                    type,
                    expression.segments.map { VkStringSegment(it) }
            )
        }
    }
}

data class VkExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        val value: String
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionLiteral): VkExpressionLiteral {
            val type = expression.type
                    ?: throw LineException("literal expression has not been assigned a type", expression)

            return VkExpressionLiteral(
                    expression.line,
                    type,
                    expression.value
            )
        }
    }
}
