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
import verik.core.ktx.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

sealed class VkExpression(
        override val line: Int,
        open val type: Symbol
): Line {

    companion object {

        operator fun invoke(expression: KtxExpression): VkExpression {
            return when (expression) {
                is KtxExpressionFunction -> VkExpressionFunction(expression)
                is KtxExpressionOperator -> VkExpressionOperator(expression)
                is KtxExpressionProperty -> VkExpressionProperty(expression)
                is KtxExpressionString -> VkExpressionString(expression)
                is KtxExpressionLiteral -> VkExpressionLiteral(expression)
            }
        }
    }
}

data class VkExpressionFunction(
        override val line: Int,
        override val type: Symbol,
        val function: Symbol,
        val target: VkExpression?,
        val args: List<VkExpression>
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtxExpressionFunction): VkExpressionFunction {
            return VkExpressionFunction(
                    expression.line,
                    expression.type,
                    expression.function,
                    expression.target?.let { VkExpression(it) },
                    expression.args.map { VkExpression(it) }
            )
        }
    }
}

data class VkExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        val identifier: VkOperatorIdentifier,
        val target: VkExpression?,
        val args: List<VkExpression>,
        val blocks: List<VkBlock>
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtxExpressionOperator): VkExpressionOperator {
            return VkExpressionOperator(
                    expression.line,
                    expression.type,
                    VkOperatorIdentifier(expression.identifier, expression.line),
                    expression.target?.let { VkExpression(it) },
                    expression.args.map { VkExpression(it) },
                    expression.blocks.map { VkBlock(it) }
            )
        }
    }
}

data class VkExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        val property: Symbol,
        val target: VkExpression?
): VkExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtxExpressionProperty): VkExpressionProperty {
            return VkExpressionProperty(
                    expression.line,
                    expression.type,
                    expression.property,
                    expression.target?.let { VkExpression(it) }
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

        operator fun invoke(expression: KtxExpressionString): VkExpressionString {
            return VkExpressionString(
                    expression.line,
                    expression.type,
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

        operator fun invoke(expression: KtxExpressionLiteral): VkExpressionLiteral {
            return VkExpressionLiteral(
                    expression.line,
                    expression.type,
                    expression.value
            )
        }
    }
}
