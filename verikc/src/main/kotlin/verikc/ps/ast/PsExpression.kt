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
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeGenerified
import verikc.base.symbol.Symbol
import verikc.ge.ast.*

sealed class PsExpression(
    open val line: Line,
    open val typeGenerified: TypeGenerified
) {

    companion object {

        operator fun invoke(expression: GeExpression): PsExpression {
            return when (expression) {
                is GeExpressionFunction -> PsExpressionFunction(expression)
                is GeExpressionOperator -> PsExpressionOperator(expression)
                is GeExpressionProperty -> PsExpressionProperty(expression)
                is GeExpressionString -> PsExpressionString(expression)
                is GeExpressionLiteral -> PsExpressionLiteral(expression)
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

    constructor(expression: GeExpressionFunction): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
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

    constructor(expression: GeExpressionOperator): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
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

    constructor(expression: GeExpressionProperty): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.propertySymbol,
        expression.receiver?.let { PsExpression(it) }
    )
}

data class PsExpressionString(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val segments: List<PsStringSegment>
): PsExpression(line, typeGenerified) {

    constructor(expression: GeExpressionString): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.segments.map { PsStringSegment(it) }
    )
}

data class PsExpressionLiteral(
    override val line: Line,
    override val typeGenerified: TypeGenerified,
    val value: LiteralValue
): PsExpression(line, typeGenerified) {

    constructor(expression: GeExpressionLiteral): this(
        expression.line,
        expression.getTypeGenerifiedNotNull(),
        expression.value
    )
}
