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

package verikc.gex.ast

import verikc.base.ast.Line
import verikc.rs.ast.RsStatement
import verikc.rs.ast.RsStatementDeclaration
import verikc.rs.ast.RsStatementExpression

sealed class GexStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: RsStatement): GexStatement {
            return when (statement) {
                is RsStatementDeclaration -> GexStatementDeclaration(statement)
                is RsStatementExpression -> GexStatementExpression(statement)
            }
        }
    }
}

data class GexStatementDeclaration(
    val property: GexProperty
): GexStatement(property.line) {

    constructor(statement: RsStatementDeclaration): this(
        GexProperty(statement.property)
    )
}

data class GexStatementExpression(
    val expression: GexExpression
): GexStatement(expression.line) {

    constructor(statement: RsStatementExpression): this(
        GexExpression(statement.expression)
    )
}
