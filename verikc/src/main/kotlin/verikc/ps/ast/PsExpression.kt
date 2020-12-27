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

import verikc.base.ast.Line
import verikc.base.ast.LiteralValue
import verikc.base.ast.TypeReified
import verikc.base.symbol.Symbol
import verikc.rf.ast.*

sealed class PsExpression(
    open val line: Line,
    open val typeReified: TypeReified
) {

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
    override val typeReified: TypeReified,
    val functionSymbol: Symbol,
    var receiver: PsExpression?,
    val args: ArrayList<PsExpression>
): PsExpression(line, typeReified) {

    constructor(expression: RfExpressionFunction): this(
        expression.line,
        expression.getTypeReifiedNotNull(),
        expression.functionSymbol,
        expression.receiver?.let { PsExpression(it) },
        ArrayList(expression.args.map { PsExpression(it) })
    )
}

data class PsExpressionOperator(
    override val line: Line,
    override val typeReified: TypeReified,
    val operatorSymbol: Symbol,
    var receiver: PsExpression?,
    val args: ArrayList<PsExpression>,
    val blocks: List<PsBlock>
): PsExpression(line, typeReified) {

    constructor(expression: RfExpressionOperator): this(
        expression.line,
        expression.getTypeReifiedNotNull(),
        expression.operatorSymbol,
        expression.receiver?.let { PsExpression(it) },
        ArrayList(expression.args.map { PsExpression(it) }),
        expression.blocks.map { PsBlock(it) }
    )
}

data class PsExpressionProperty(
    override val line: Line,
    override val typeReified: TypeReified,
    val propertySymbol: Symbol,
    var receiver: PsExpression?
): PsExpression(line, typeReified) {

    constructor(expression: RfExpressionProperty): this(
        expression.line,
        expression.getTypeReifiedNotNull(),
        expression.propertySymbol,
        expression.receiver?.let { PsExpression(it) }
    )
}

data class PsExpressionString(
    override val line: Line,
    override val typeReified: TypeReified,
    val segments: List<PsStringSegment>
): PsExpression(line, typeReified) {

    constructor(expression: RfExpressionString): this(
        expression.line,
        expression.getTypeReifiedNotNull(),
        expression.segments.map { PsStringSegment(it) }
    )
}

data class PsExpressionLiteral(
    override val line: Line,
    override val typeReified: TypeReified,
    val value: LiteralValue
): PsExpression(line, typeReified) {

    constructor(expression: RfExpressionLiteral): this(
        expression.line,
        expression.getTypeReifiedNotNull(),
        expression.value
    )
}
