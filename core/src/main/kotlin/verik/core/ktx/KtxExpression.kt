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

package verik.core.ktx

import verik.core.kt.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

sealed class KtxExpression(
        override val line: Int,
        open val type: Symbol
): Line {

    companion object {

        operator fun invoke(expression: KtExpression): KtxExpression {
            return when (expression) {
                is KtExpressionFunction -> KtxExpressionFunction(expression)
                is KtExpressionOperator -> KtxExpressionOperator(expression)
                is KtExpressionProperty -> KtxExpressionProperty(expression)
                is KtExpressionString -> KtxExpressionString(expression)
                is KtExpressionLiteral -> KtxExpressionLiteral(expression)
            }
        }
    }
}

data class KtxExpressionFunction(
        override val line: Int,
        override val type: Symbol,
        val function: Symbol,
        val target: KtxExpression?,
        val args: List<KtxExpression>
): KtxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionFunction): KtxExpressionFunction {
            val type = expression.type
                    ?: throw LineException("function expression has not been assigned a type", expression)
            val function = expression.function
                    ?: throw LineException("function expression has not been resolved", expression)

            return KtxExpressionFunction(
                    expression.line,
                    type,
                    function,
                    expression.target?.let { KtxExpression(it) },
                    expression.args.map { KtxExpression(it) }
            )
        }
    }
}

data class KtxExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        val identifier: KtOperatorIdentifier,
        val target: KtxExpression?,
        val args: List<KtxExpression>,
        val blocks: List<KtxBlock>
): KtxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionOperator): KtxExpressionOperator {
            val type = expression.type
                    ?: throw LineException("operator expression has not been assigned a type", expression)

            return KtxExpressionOperator(
                    expression.line,
                    type,
                    expression.identifier,
                    expression.target?.let { KtxExpression(it) },
                    expression.args.map { KtxExpression(it) },
                    expression.blocks.map { KtxBlock(it) }
            )
        }
    }
}

data class KtxExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        val property: Symbol,
        val target: KtxExpression?
): KtxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionProperty): KtxExpressionProperty {
            val type = expression.type
                    ?: throw LineException("property expression has not been assigned a type", expression)
            val property = expression.property
                    ?: throw LineException("property expression has not been resolved", expression)

            return KtxExpressionProperty(
                    expression.line,
                    type,
                    property,
                    expression.target?.let { KtxExpression(it) }
            )
        }
    }
}

data class KtxExpressionString(
        override val line: Int,
        override val type: Symbol,
        val segments: List<KtxStringSegment>
): KtxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionString): KtxExpressionString {
            val type = expression.type
                    ?: throw LineException("string expression has not been assigned a type", expression)

            return KtxExpressionString(
                    expression.line,
                    type,
                    expression.segments.map { KtxStringSegment(it) }
            )
        }
    }
}

data class KtxExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        val value: String
): KtxExpression(line, type) {

    companion object {

        operator fun invoke(expression: KtExpressionLiteral): KtxExpressionLiteral {
            val type = expression.type
                    ?: throw LineException("literal expression has not been assigned a type", expression)

            return KtxExpressionLiteral(
                    expression.line,
                    type,
                    expression.value
            )
        }
    }
}
