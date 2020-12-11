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

package verikc.ps.ast

import verikc.base.ast.*
import verikc.ps.extract.PsExpressionExtractor
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.ast.*
import verikc.sv.ast.SvExpression

sealed class PsExpression(
        open val line: Line,
        open val reifiedType: ReifiedType
) {

    fun extract(symbolTable: PsSymbolTable): SvExpression {
        return PsExpressionExtractor.extract(this, symbolTable)
    }

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
        override val line: Line,
        override val reifiedType: ReifiedType,
        val function: Symbol,
        var receiver: PsExpression?,
        val args: ArrayList<PsExpression>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionFunction): PsExpressionFunction {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("function expression has not been reified", expression.line)

            return PsExpressionFunction(
                    expression.line,
                    reifiedType,
                    expression.function,
                    expression.receiver?.let { PsExpression(it) },
                    ArrayList(expression.args.map { PsExpression(it) })
            )
        }
    }
}

data class PsExpressionOperator(
        override val line: Line,
        override val reifiedType: ReifiedType,
        val operator: Symbol,
        var receiver: PsExpression?,
        val args: ArrayList<PsExpression>,
        val blocks: List<PsBlock>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionOperator): PsExpressionOperator {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("operator expression has not been reified", expression.line)

            return PsExpressionOperator(
                    expression.line,
                    reifiedType,
                    expression.operator,
                    expression.receiver?.let { PsExpression(it) },
                    ArrayList(expression.args.map { PsExpression(it) }),
                    expression.blocks.map { PsBlock(it) }
            )
        }
    }
}

data class PsExpressionProperty(
        override val line: Line,
        override val reifiedType: ReifiedType,
        val property: Symbol,
        var receiver: PsExpression?
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionProperty): PsExpressionProperty {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("property expression has not been reified", expression.line)

            return PsExpressionProperty(
                    expression.line,
                    reifiedType,
                    expression.property,
                    expression.receiver?.let { PsExpression(it) }
            )
        }
    }
}

data class PsExpressionString(
        override val line: Line,
        override val reifiedType: ReifiedType,
        val segments: List<PsStringSegment>
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionString): PsExpressionString {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("string expression has not been reified", expression.line)

            return PsExpressionString(
                    expression.line,
                    reifiedType,
                    expression.segments.map { PsStringSegment(it) }
            )
        }
    }
}

data class PsExpressionLiteral(
        override val line: Line,
        override val reifiedType: ReifiedType,
        val value: LiteralValue
): PsExpression(line, reifiedType) {

    companion object {

        operator fun invoke(expression: RfExpressionLiteral): PsExpressionLiteral {
            val reifiedType = expression.reifiedType
                    ?: throw LineException("literal expression has not been reified", expression.line)

            return PsExpressionLiteral(
                    expression.line,
                    reifiedType,
                    expression.value
            )
        }
    }
}
