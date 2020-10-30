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

package verik.core.rf.ast

import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.base.ast.LiteralValue
import verik.core.base.ast.Symbol
import verik.core.rf.extract.RfExpressionExtractor
import verik.core.rf.symbol.RfSymbolTable
import verik.core.sv.ast.SvExpression
import verik.core.sv.ast.SvStatement
import verik.core.sv.ast.SvStatementExpression
import verik.core.vk.ast.*

sealed class RfExpression(
        override val line: Int,
        open val type: Symbol,
        open var reifiedType: RfReifiedType?
): Line {

    fun extract(symbolTable: RfSymbolTable): SvStatement {
        return RfExpressionExtractor.extract(this, symbolTable)
    }

    fun extractAsExpression(symbolTable: RfSymbolTable): SvExpression {
        return RfExpressionExtractor.extract(this, symbolTable).let {
            if (it is SvStatementExpression) it.expression
            else throw LineException("expression expected from extraction", it)
        }
    }

    companion object {

        operator fun invoke(expression: VkExpression): RfExpression {
            return when (expression) {
                is VkExpressionFunction -> RfExpressionFunction(expression)
                is VkExpressionOperator -> RfExpressionOperator(expression)
                is VkExpressionProperty -> RfExpressionProperty(expression)
                is VkExpressionString -> RfExpressionString(expression)
                is VkExpressionLiteral -> RfExpressionLiteral(expression)
            }
        }
    }
}

data class RfExpressionFunction(
        override val line: Int,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val function: Symbol,
        val receiver: RfExpression?,
        val args: List<RfExpression>
): RfExpression(line, type, reifiedType) {

    constructor(expression: VkExpressionFunction): this(
            expression.line,
            expression.type,
            null,
            expression.function,
            expression.receiver?.let { RfExpression(it) },
            expression.args.map { RfExpression(it) }
    )
}

data class RfExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val operator: Symbol,
        val receiver: RfExpression?,
        val args: List<RfExpression>,
        val blocks: List<RfBlock>
): RfExpression(line, type, reifiedType) {

    constructor(expression: VkExpressionOperator): this(
        expression.line,
        expression.type,
        null,
        expression.operator,
        expression.receiver?.let { RfExpression(it) },
        expression.args.map { RfExpression(it) },
        expression.blocks.map { RfBlock(it) }
    )
}

data class RfExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val property: Symbol,
        val receiver: RfExpression?
): RfExpression(line, type, reifiedType) {

    constructor(expression: VkExpressionProperty): this(
            expression.line,
            expression.type,
            null,
            expression.property,
            expression.receiver?.let { RfExpression(it) }
    )
}

data class RfExpressionString(
        override val line: Int,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val segments: List<RfStringSegment>
): RfExpression(line, type, reifiedType) {

    constructor(expression: VkExpressionString): this(
            expression.line,
            expression.type,
            null,
            expression.segments.map { RfStringSegment(it) }
    )
}

data class RfExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        override var reifiedType: RfReifiedType?,
        val value: LiteralValue
): RfExpression(line, type, reifiedType) {

    constructor(expression: VkExpressionLiteral): this(
            expression.line,
            expression.type,
            null,
            expression.value
    )
}