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

package verik.core.it

import verik.core.base.Line
import verik.core.base.LiteralValue
import verik.core.base.Symbol
import verik.core.it.extract.ItExpressionExtractor
import verik.core.it.symbol.ItSymbolTable
import verik.core.sv.SvExpression
import verik.core.vk.*

sealed class ItExpression(
        override val line: Int,
        open val type: Symbol,
        open var typeReified: ItTypeReified?
): Line {

    fun extract(symbolTable: ItSymbolTable): SvExpression {
        return ItExpressionExtractor.extract(this, symbolTable)
    }

    companion object {

        operator fun invoke(expression: VkExpression): ItExpression {
            return when (expression) {
                is VkExpressionFunction -> ItExpressionFunction(expression)
                is VkExpressionOperator -> ItExpressionOperator(expression)
                is VkExpressionProperty -> ItExpressionProperty(expression)
                is VkExpressionString -> ItExpressionString(expression)
                is VkExpressionLiteral -> ItExpressionLiteral(expression)
            }
        }
    }
}

data class ItExpressionFunction(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val function: Symbol,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItExpression(line, type, typeReified) {

    constructor(expression: VkExpressionFunction): this(
            expression.line,
            expression.type,
            null,
            expression.function,
            expression.target?.let { ItExpression(it) },
            expression.args.map { ItExpression(it) }
    )
}

data class ItExpressionOperator(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val identifier: ItOperatorIdentifier,
        val target: ItExpression?,
        val args: List<ItExpression>
): ItExpression(line, type, typeReified) {

    constructor(expression: VkExpressionOperator): this(
        expression.line,
        expression.type,
        null,
        ItOperatorIdentifier(expression.identifier, expression),
        expression.target?.let { ItExpression(it) },
        expression.args.map { ItExpression(it) }
    )
}

data class ItExpressionProperty(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val property: Symbol,
        val target: ItExpression?
): ItExpression(line, type, typeReified) {

    constructor(expression: VkExpressionProperty): this(
            expression.line,
            expression.type,
            null,
            expression.property,
            expression.target?.let { ItExpression(it) }
    )
}

data class ItExpressionString(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val segments: List<ItStringSegment>
): ItExpression(line, type, typeReified) {

    constructor(expression: VkExpressionString): this(
            expression.line,
            expression.type,
            null,
            expression.segments.map { ItStringSegment(it) }
    )
}

data class ItExpressionLiteral(
        override val line: Int,
        override val type: Symbol,
        override var typeReified: ItTypeReified?,
        val value: LiteralValue
): ItExpression(line, type, typeReified) {

    constructor(expression: VkExpressionLiteral): this(
            expression.line,
            expression.type,
            null,
            expression.value
    )
}