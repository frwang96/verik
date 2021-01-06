/*
 * Copyright (c) 2021 Francis Wang
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

package verikc.ge.reify

import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.ge.ast.GeBlock
import verikc.ge.ast.GeStatementDeclaration
import verikc.ge.ast.GeStatementExpression
import verikc.ge.symbol.GeSymbolTable

object GeReifierBlock {

    fun reify(block: GeBlock, symbolTable: GeSymbolTable) {
        block.statements.map {
            when (it) {
                is GeStatementDeclaration -> {
                    GeReifierExpression.reify(it.primaryProperty.expression, symbolTable)
                    val typeReified = it.primaryProperty.expression.getTypeReifiedNotNull()
                    if (typeReified.typeClass != INSTANCE)
                        throw LineException("property should be initialized", it.line)
                    it.primaryProperty.typeReified = typeReified
                    symbolTable.addProperty(it.primaryProperty)
                }
                is GeStatementExpression -> {
                    GeReifierExpression.reify(it.expression, symbolTable)
                }
            }
        }
    }
}