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

package verikc.ge.generify

import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.ge.ast.GeBlock
import verikc.ge.ast.GeStatementDeclaration
import verikc.ge.ast.GeStatementExpression
import verikc.ge.table.GeSymbolTable

object GeGenerifierBlock {

    fun generify(block: GeBlock, symbolTable: GeSymbolTable) {
        block.statements.map {
            when (it) {
                is GeStatementDeclaration -> {
                    if (it.property.expression == null)
                        throw LineException("property expression expected", it.line)
                    GeGenerifierExpression.generify(it.property.expression, symbolTable)
                    val typeGenerified = it.property.expression.getTypeGenerifiedNotNull()
                    if (typeGenerified.typeClass != INSTANCE)
                        throw LineException("property should be initialized", it.line)
                    it.property.typeGenerified = typeGenerified
                    symbolTable.addProperty(it.property)
                }
                is GeStatementExpression -> {
                    GeGenerifierExpression.generify(it.expression, symbolTable)
                }
            }
        }
    }
}