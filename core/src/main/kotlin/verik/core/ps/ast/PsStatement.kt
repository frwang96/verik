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

package verik.core.ps.ast

import verik.core.base.ast.Line
import verik.core.ps.symbol.PsSymbolTable
import verik.core.rf.ast.RfStatement
import verik.core.rf.ast.RfStatementExpression
import verik.core.sv.ast.SvStatement
import verik.core.sv.ast.SvStatementExpression

sealed class PsStatement(
        override val line: Int
): Line {

    abstract fun extract(symbolTable: PsSymbolTable): SvStatement

    companion object {

        operator fun invoke(statement: RfStatement): PsStatement {
            return when (statement) {
                is RfStatementExpression -> PsStatementExpression(statement)
            }
        }
    }
}

data class PsStatementExpression(
        override val line: Int,
        var expression: PsExpression
): PsStatement(line) {

    override fun extract(symbolTable: PsSymbolTable): SvStatement {
        return SvStatementExpression(expression.extract(symbolTable))
    }

    constructor(statement: RfStatementExpression): this(
            statement.line,
            PsExpression(statement.expression)
    )
}
