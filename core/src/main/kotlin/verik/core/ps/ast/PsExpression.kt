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

package verik.core.ps.ast

import verik.core.base.Line
import verik.core.base.LineException
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.rf.ast.*

sealed class PsExpression(
        override val line: Int,
        open val reifiedType: PsReifiedType
): Line {

    companion object {

        operator fun invoke(expression: RfExpression): PsExpression {
            return when (expression) {
                is RfExpressionFunction -> PsExpressionFunction(expression)
                is RfExpressionOperator -> PsExpressionOperator(expression)
                is RfExpressionProperty -> PsExpressionProperty(expression)
                is RfExpressionString -> PsExpressionString(expression)
                is RfExpressionLiteral -> PsExpressionLiteral(expression)
            }
        }
    }
}

data class PsExpressionFunction(
        override val line: Int,
        override val reifiedType: PsReifiedType,
        val function: Symbol,
        var receiver: PsExpression?,
        val args: ArrayList<PsExpression>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionFunction): PsExpressionFunction {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("function expression has not been reified", expression)

            return PsExpressionFunction(
                    expression.line,
                    PsReifiedType(reifiedType),
                    expression.function,
                    expression.receiver?.let { PsExpression(it) },
                    ArrayList(expression.args.map { PsExpression(it) })
            )
        }
    }
}

data class PsExpressionOperator(
        override val line: Int,
        override val reifiedType: PsReifiedType,
        val operator: Symbol,
        var receiver: PsExpression?,
        val args: ArrayList<PsExpression>,
        val blocks: List<PsBlock>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionOperator): PsExpressionOperator {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("operator expression has not been reified", expression)

            return PsExpressionOperator(
                    expression.line,
                    PsReifiedType(reifiedType),
                    expression.operator,
                    expression.receiver?.let { PsExpression(it) },
                    ArrayList(expression.args.map { PsExpression(it) }),
                    expression.blocks.map { PsBlock(it) }
            )
        }
    }
}

data class PsExpressionProperty(
        override val line: Int,
        override val reifiedType: PsReifiedType,
        val property: Symbol,
        var receiver: PsExpression?
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionProperty): PsExpressionProperty {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("property expression has not been reified", expression)

            return PsExpressionProperty(
                    expression.line,
                    PsReifiedType(reifiedType),
                    expression.property,
                    expression.receiver?.let { PsExpression(it) }
            )
        }
    }
}

data class PsExpressionString(
        override val line: Int,
        override val reifiedType: PsReifiedType,
        val segments: List<PsStringSegment>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionString): PsExpressionString {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("string expression has not been reified", expression)

            return PsExpressionString(
                    expression.line,
                    PsReifiedType(reifiedType),
                    expression.segments.map { PsStringSegment(it) }
            )
        }
    }
}

data class PsExpressionLiteral(
        override val line: Int,
        override val reifiedType: PsReifiedType,
        val value: LiteralValue
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionLiteral): PsExpressionLiteral {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("literal expression has not been reified", expression)

            return PsExpressionLiteral(
                    expression.line,
                    PsReifiedType(reifiedType),
                    expression.value
            )
        }
    }
}
