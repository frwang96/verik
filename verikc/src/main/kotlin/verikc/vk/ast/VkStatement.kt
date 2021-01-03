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

package verikc.vk.ast

import verikc.base.ast.Line
import verikc.kt.ast.KtStatement
import verikc.kt.ast.KtStatementDeclaration
import verikc.kt.ast.KtStatementExpression
import verikc.vk.build.VkBuilderPrimaryProperty

sealed class VkStatement(
    open val line: Line
) {

    companion object {

        operator fun invoke(statement: KtStatement): VkStatement {
            return when (statement) {
                is KtStatementDeclaration -> VkStatementDeclaration(statement)
                is KtStatementExpression -> VkStatementExpression(statement)
            }
        }
    }
}

data class VkStatementDeclaration(
    val primaryProperty: VkPrimaryProperty
): VkStatement(primaryProperty.line) {

    constructor(statement: KtStatementDeclaration): this(
        VkBuilderPrimaryProperty.build(statement.primaryProperty)
    )
}

data class VkStatementExpression(
    val expression: VkExpression
): VkStatement(expression.line) {

    constructor(statement: KtStatementExpression): this(
        VkExpression(statement.expression)
    )
}

