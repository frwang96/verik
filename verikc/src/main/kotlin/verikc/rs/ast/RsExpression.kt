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

package verikc.rs.ast

import verikc.base.ast.*
import verikc.base.symbol.Symbol
import verikc.kt.ast.*

sealed class RsExpression(
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

        operator fun invoke(expression: KtExpression): RsExpression {
            return when (expression) {
                is KtExpressionFunction -> RsExpressionFunction(expression)
                is KtExpressionOperator -> RsExpressionOperator(expression)
                is KtExpressionProperty -> RsExpressionProperty(expression)
                is KtExpressionLiteral -> RsExpressionLiteral(expression)
            }
        }
    }
}

data class RsExpressionFunction(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val identifier: String,
    val receiver: RsExpression?,
    val argIdentifiers: List<String>?,
    val args: List<RsExpression>,
    var functionSymbol: Symbol?
): RsExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionFunction): this(
        expression.line,
        null,
        null,
        expression.identifier,
        expression.receiver?.let { RsExpression(it) },
        expression.argIdentifiers,
        expression.args.map { RsExpression(it) },
        null
    )

    fun getFunctionSymbolNotNull(): Symbol {
        return functionSymbol
            ?: throw LineException("expression has not been resolved", line)
    }
}

data class RsExpressionOperator(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val operatorSymbol: Symbol,
    val receiver: RsExpression?,
    val args: List<RsExpression>,
    val blocks: List<RsBlock>
): RsExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionOperator): this(
        expression.line,
        null,
        null,
        expression.operatorSymbol,
        expression.receiver?.let { RsExpression(it) },
        expression.args.map { RsExpression(it) },
        expression.blocks.map { RsBlock(it) }
    )
}

data class RsExpressionProperty(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val identifier: String,
    val receiver: RsExpression?,
    var propertySymbol: Symbol?
): RsExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionProperty): this(
        expression.line,
        null,
        null,
        expression.identifier,
        expression.receiver?.let { RsExpression(it) },
        null
    )

    fun getPropertySymbolNotNull(): Symbol {
        return propertySymbol
            ?: throw LineException("expression has not been resolved", line)
    }
}

data class RsExpressionLiteral(
    override val line: Line,
    override var typeGenerified: TypeGenerified?,
    override var expressionClass: ExpressionClass?,
    val string: String,
    var value: LiteralValue?
): RsExpression(line, typeGenerified, expressionClass) {

    constructor(expression: KtExpressionLiteral): this(
        expression.line,
        null,
        null,
        expression.string,
        null
    )

    fun getValueNotNull(): LiteralValue {
        return value
            ?: throw LineException("expression has not been resolved", line)
    }
}
