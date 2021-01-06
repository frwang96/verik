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

import verikc.base.ast.Line
import verikc.kt.ast.KtStatement
import verikc.kt.ast.KtStatementDeclaration
import verikc.kt.ast.KtStatementExpression

sealed class RsStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: KtStatement): RsStatement {
            return when (statement) {
                is KtStatementDeclaration -> RsStatementDeclaration(statement)
                is KtStatementExpression -> RsStatementExpression(statement)
            }
        }
    }
}

data class RsStatementDeclaration(
    val primaryProperty: RsPrimaryProperty
): RsStatement(primaryProperty.line) {

    constructor(statement: KtStatementDeclaration): this(
        RsPrimaryProperty(statement.primaryProperty)
    )
}

data class RsStatementExpression(
    val expression: RsExpression
): RsStatement(expression.line) {

    constructor(statement: KtStatementExpression): this(
        RsExpression(statement.expression)
    )
}
