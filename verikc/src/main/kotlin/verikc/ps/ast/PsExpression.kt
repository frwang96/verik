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

package verikc.ps.ast

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.vk.ast.*

sealed class PsExpression(
    open val line: Line,
    open val typeGenerified: TypeGenerified
) {

    abstract fun copy(line: Line): PsExpression

    companion object {

        operator fun invoke(expression: VkExpression): PsExpression {
            return when (expression) {
                is VkExpressionFunction -> PsExpressionFunction(expression)
                is VkExpressionOperator -> PsExpressionOperator(expression)
                is VkExpressionProperty -> PsExpressionProperty(expression)
                is VkExpressionString -> PsExpressionString(expression)
                is VkExpressionLiteral -> PsExpressionLiteral(expression)
            }
        }
    }
}

data class PsExpressionFunction(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val functionSymbol: Symbol,
    var receiver: PsExpression?,
    val args: ArrayList<PsExpression>
): PsExpression(line, typeGenerified) {

    override fun copy(line: Line): PsExpression {
        return PsExpressionFunction(
            line,
            this.typeGenerified,
            this.functionSymbol,
            this.receiver?.copy(line),
            ArrayList(this.args.map { it.copy(line) })
        )
    }

    constructor(expression: VkExpressionFunction): this(
        expression.line,
        expression.typeGenerified,
        expression.functionSymbol,
        expression.receiver?.let { PsExpression(it) },
        ArrayList(expression.args.map { PsExpression(it) })
    )
}

data class PsExpressionOperator(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val operatorSymbol: Symbol,
    var receiver: PsExpression?,
    val args: ArrayList<PsExpression>,
    val blocks: List<PsBlock>
): PsExpression(line, typeGenerified) {

    override fun copy(line: Line): PsExpression {
        throw LineException("copying of operator expression not supported", line)
    }

    constructor(expression: VkExpressionOperator): this(
        expression.line,
        expression.typeGenerified,
        expression.operatorSymbol,
        expression.receiver?.let { PsExpression(it) },
        ArrayList(expression.args.map { PsExpression(it) }),
        expression.blocks.map { PsBlock(it) }
    )
}

data class PsExpressionProperty(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val propertySymbol: Symbol,
    var receiver: PsExpression?
): PsExpression(line, typeGenerified) {

    override fun copy(line: Line): PsExpression {
        return PsExpressionProperty(
            line,
            this.typeGenerified,
            this.propertySymbol,
            this.receiver?.copy(line)
        )
    }

    constructor(expression: VkExpressionProperty): this(
        expression.line,
        expression.typeGenerified,
        expression.propertySymbol,
        expression.receiver?.let { PsExpression(it) }
    )
}

data class PsExpressionString(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val segments: List<PsStringSegment>
): PsExpression(line, typeGenerified) {

    override fun copy(line: Line): PsExpression {
        throw LineException("copying of string expression not supported", line)
    }

    constructor(expression: VkExpressionString): this(
        expression.line,
        expression.typeGenerified,
        expression.segments.map { PsStringSegment(it) }
    )
}

data class PsExpressionLiteral(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val value: LiteralValue
): PsExpression(line, typeGenerified) {

    override fun copy(line: Line): PsExpression {
        return PsExpressionLiteral(
            line,
            this.typeGenerified,
            this.value
        )
    }

    constructor(expression: VkExpressionLiteral): this(
        expression.line,
        expression.typeGenerified,
        expression.value
    )
}
