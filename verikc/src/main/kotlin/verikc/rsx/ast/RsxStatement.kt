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

import verikc.base.ast.Line
import verikc.kt.ast.KtStatement
import verikc.kt.ast.KtStatementDeclaration
import verikc.kt.ast.KtStatementExpression

sealed class RsxStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: KtStatement): RsxStatement {
            return when (statement) {
                is KtStatementDeclaration -> RsxStatementDeclaration(statement)
                is KtStatementExpression -> RsxStatementExpression(statement)
            }
        }
    }
}

data class RsxStatementDeclaration(
    val property: RsxProperty
): RsxStatement(property.line) {

    constructor(statement: KtStatementDeclaration): this(
        RsxProperty(statement.property)
    )
}

data class RsxStatementExpression(
    val expression: RsxExpression
): RsxStatement(expression.line) {

    constructor(statement: KtStatementExpression): this(
        RsxExpression(statement.expression)
    )
}
