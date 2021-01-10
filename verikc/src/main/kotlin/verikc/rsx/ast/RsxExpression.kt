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

package verikc.rsx.ast

import verikc.base.ast.*
import verikc.base.symbol.Symbol
import verikc.kt.ast.*

sealed class RsxExpression(
    open val line: Line,
    open var typeGenerified: TypeGenerified?,
    open var expressionClass: ExpressionClass?
) {

    fun getTypeGenerifiedNotNull(): TypeGenerified {
        return typeGenerified
            ?: throw LineException("expression has not been resolved", line)
    }

    fun getExpressionClassNotNull(): ExpressionClass {
        return expressionClass
            ?: throw LineException("expression has not been resolved", line)
    }

    companion object {

        operator fun invoke(expression: KtExpression): RsxExpression {
            return when (expression) {
                is KtExpressionFunction -> RsxExpressionFunction(expression)
                is KtExpressionOperator -> RsxExpressionOperator(expression)
                is KtExpressionProperty -> RsxExpressionProperty(expression)
                is KtExpressionString -> RsxExpressionString(expression)
                is KtExpressionLiteral -> RsxExpressionLiteral(expression)
            }
        }
    }
}

data class RsxExpressionFunction(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val identifier: String,
    val receiver: RsxExpression?,
    val args: List<RsxExpression>,
    var functionSymbol: Symbol?
): RsxExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionFunction): this(
        expression.line,
        null,
        null,
        expression.identifier,
        expression.receiver?.let { RsxExpression(it) },
        expression.args.map { RsxExpression(it) },
        null
    )
}

data class RsxExpressionOperator(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val operatorSymbol: Symbol,
    val receiver: RsxExpression?,
    val args: List<RsxExpression>,
    val blocks: List<RsxBlock>
): RsxExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionOperator): this(
        expression.line,
        null,
        null,
        expression.operatorSymbol,
        expression.receiver?.let { RsxExpression(it) },
        expression.args.map { RsxExpression(it) },
        expression.blocks.map { RsxBlock(it) }
    )
}

data class RsxExpressionProperty(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val identifier: String,
    val receiver: RsxExpression?,
    var propertySymbol: Symbol?
): RsxExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionProperty): this(
        expression.line,
        null,
        null,
        expression.identifier,
        expression.receiver?.let { RsxExpression(it) },
        null
    )
}

data class RsxExpressionString(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val segments: List<RsxStringSegment>
): RsxExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionString): this(
        expression.line,
        null,
        null,
        expression.segments.map { RsxStringSegment(it) }
    )
}

data class RsxExpressionLiteral(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val string: String,
    var value: LiteralValue?
): RsxExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionLiteral): this(
        expression.line,
        null,
        null,
        expression.string,
        null
    )
}
