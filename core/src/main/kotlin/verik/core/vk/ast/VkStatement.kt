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

package verik.core.vk.ast

import verik.core.base.ast.Line
import verik.core.base.ast.LineException
import verik.core.kt.ast.KtStatement
import verik.core.kt.ast.KtStatementDeclaration
import verik.core.kt.ast.KtStatementExpression

sealed class VkStatement(
        override val line: Int
): Line {

    companion object {

        operator fun invoke(statement: KtStatement): VkStatement {
            return when (statement) {
                is KtStatementDeclaration -> throw LineException("declaration statements not supported", statement)
                is KtStatementExpression -> VkStatementExpression(statement)
            }
        }
    }
}

data class VkStatementExpression(
        val expression: VkExpression
): VkStatement(expression.line) {

    constructor(statement: KtStatementExpression): this(
            VkExpression(statement.expression)
    )
}