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

package verik.core.vkx

import verik.core.kt.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.symbol.Symbol

sealed class VkxExpression(
        override val line: Int,
        open val type: Symbol
): Line {

    companion object {

        operator fun invoke(expression: KtExpression): VkxExpression {
            return when (expression) {
                is KtExpressionFunction -> VkxExpressionFunction(expression)
                is KtExpressionOperator -> VkxExpressionOperator(expression)
                is KtExpressionProperty -> VkxExpressionProperty(expression)
                is KtExpressionString -> VkxExpressionString(expression)
                is KtExpressionLiteral -> VkxExpressionLiteral(expression)
            }
        }
    }
}

data class VkxExpressionFunction(
        override val line: Int,
        override val type: Symbol,
        val target: VkxExpression?,
        val args: List<VkxExpression>,
        val function: Symbol
): VkxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionFunction): VkxExpressionFunction {
            val type = expression.type
            val function = expression.function

            if (type == null) {
                throw LineException("function expression has not been assigned a type", expression)
            }
            if (function == null) {
                throw LineException("function has not been resolved", expression)
            }

            return VkxExpressionFunction(
                    expression.line,
                    type,
                    expression.target?.let { VkxExpression(it) },
                    expression.args.map { VkxExpression(it) },
                    function
            )
        }
    }
}

data class VkxExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        val target: VkxExpression?,
        val identifier: VkxOperatorIdentifier,
        val args: List<VkxExpression>,
        val blocks: List<VkxBlock>
): VkxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionOperator): VkxExpressionOperator {
            val type = expression.type
            if (type != null) {
                return VkxExpressionOperator(
                        expression.line,
                        type,
                        expression.target?.let { VkxExpression(it) },
                        VkxOperatorIdentifier(expression.identifier, expression.line),
                        expression.args.map { VkxExpression(it) },
                        expression.blocks.map { VkxBlock(it) }
                )
            } else {
                throw LineException("operator expression has not been assigned a type", expression)
            }
        }
    }
}

data class VkxExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        val target: VkxExpression?,
        val property: Symbol?
): VkxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionProperty): VkxExpressionProperty {
            val type = expression.type
            if (type != null) {
                return VkxExpressionProperty(
                        expression.line,
                        type,
                        expression.target?.let { VkxExpression(it) },
                        expression.property
                )
            } else {
                throw LineException("property expression has not been assigned a type", expression)
            }
        }
    }
}

data class VkxExpressionString(
        override val line: Int,
        override val type: Symbol,
        val segments: List<VkxStringSegment>
): VkxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionString): VkxExpressionString {
            val type = expression.type
            if (type != null) {
                return VkxExpressionString(
                        expression.line,
                        type,
                        expression.segments.map { VkxStringSegment(it) }
                )
            } else {
                throw LineException("string expression has not been assigned a type", expression)
            }
        }
    }
}

data class VkxExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        val value: String
): VkxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionLiteral): VkxExpressionLiteral {
            val type = expression.type
            if (type != null) {
                return VkxExpressionLiteral(
                        expression.line,
                        type,
                        expression.value
                )
            } else {
                throw LineException("literal expression has not been assigned a type", expression)
            }
        }
    }
}
