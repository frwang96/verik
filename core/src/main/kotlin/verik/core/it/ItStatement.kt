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
import verik.core.it.symbol.ItSymbolTable
import verik.core.sv.SvStatement
import verik.core.sv.SvStatementExpression
import verik.core.vk.VkStatement
import verik.core.vk.VkStatementExpression

sealed class ItStatement(
        override val line: Int
): Line {

    abstract fun extract(symbolTable: ItSymbolTable): SvStatement

    companion object {

        operator fun invoke(statement: VkStatement): ItStatement {
            return when (statement) {
                is VkStatementExpression -> ItStatementExpression(statement)
            }
        }
    }
}

data class ItStatementExpression(
        override val line: Int,
        val expression: ItExpression
): ItStatement(line) {

    override fun extract(symbolTable: ItSymbolTable): SvStatement {
        return SvStatementExpression(
                line,
                expression.extract(symbolTable)
        )
    }

    constructor(statement: VkStatementExpression): this(
            statement.line,
            ItExpression(statement.expression)
    )
}