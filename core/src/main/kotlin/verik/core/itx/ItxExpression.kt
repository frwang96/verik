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

package verik.core.itx

import verik.core.it.*
import verik.core.main.Line
import verik.core.main.LineException
import verik.core.main.symbol.Symbol

sealed class ItxExpression(
        override val line: Int,
        open val typeReified: ItTypeReified
): Line {

    companion object {

        operator fun invoke(expression: ItExpression): ItxExpression {
            return when (expression) {
                is ItExpressionFunction -> ItxExpressionFunction(expression)
                is ItExpressionOperator -> ItxExpressionOperator(expression)
                is ItExpressionProperty -> ItxExpressionProperty(expression)
                is ItExpressionString -> ItxExpressionString(expression)
                is ItExpressionLiteral -> ItxExpressionLiteral(expression)
            }
        }
    }
}

data class ItxExpressionFunction(
        override val line: Int,
        override val typeReified: ItTypeReified,
        val function: Symbol,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItxExpression(line, typeReified) {

    companion object {

        operator fun invoke(expression: ItExpressionFunction): ItxExpressionFunction {
            val typeReified = expression.typeReified
                    ?: throw LineException("type of function expression has not been reified", expression)

            return ItxExpressionFunction(
                    expression.line,
                    typeReified,
                    expression.function,
                    expression.target,
                    expression.args
            )
        }
    }
}

data class ItxExpressionOperator(
        override val line: Int,
        override val typeReified: ItTypeReified,
        val identifier: ItOperatorIdentifier,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItxExpression(line, typeReified) {

    companion object {

        operator fun invoke(expression: ItExpressionOperator): ItxExpressionOperator {
            val typeReified = expression.typeReified
                    ?: throw LineException("type of operator expression has not been reified", expression)

            return ItxExpressionOperator(
                    expression.line,
                    typeReified,
                    expression.identifier,
                    expression.target,
                    expression.args
            )
        }
    }
}

data class ItxExpressionProperty(
        override val line: Int,
        override val typeReified: ItTypeReified,
        val property: Symbol,
        val target: ItExpression?
): ItxExpression(line, typeReified) {

    companion object {

        operator fun invoke(expression: ItExpressionProperty): ItxExpressionProperty {
            val typeReified = expression.typeReified
                    ?: throw LineException("type of property expression has not been reified", expression)

            return ItxExpressionProperty(
                    expression.line,
                    typeReified,
                    expression.property,
                    expression.target
            )
        }
    }
}

data class ItxExpressionString(
        override val line: Int,
        override val typeReified: ItTypeReified,
        val segments: List<ItStringSegment>
): ItxExpression(line, typeReified) {

    companion object {

        operator fun invoke(expression: ItExpressionString): ItxExpressionString {

            val typeReified = expression.typeReified
                    ?: throw LineException("type of string expression has not been reified", expression)

            return ItxExpressionString(
                    expression.line,
                    typeReified,
                    expression.segments
            )
        }
    }
}

data class ItxExpressionLiteral(
        override val line: Int,
        override val typeReified: ItTypeReified,
        val value: String
): ItxExpression(line, typeReified) {

    companion object {

        operator fun invoke(expression: ItExpressionLiteral): ItxExpressionLiteral {

            val typeReified = expression.typeReified
                    ?: throw LineException("type of literal expression has not been reified", expression)

            return ItxExpressionLiteral(
                    expression.line,
                    typeReified,
                    expression.value
            )
        }
    }
}
