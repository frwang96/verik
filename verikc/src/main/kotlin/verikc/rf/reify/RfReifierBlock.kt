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

package verikc.rf.reify

import verikc.base.ast.LineException
import verikc.base.ast.TypeClass.INSTANCE
import verikc.rf.ast.RfBlock
import verikc.rf.ast.RfStatementDeclaration
import verikc.rf.ast.RfStatementExpression
import verikc.rf.symbol.RfSymbolTable

object RfReifierBlock {

    fun reify(block: RfBlock, symbolTable: RfSymbolTable) {
        block.statements.map {
            when (it) {
                is RfStatementDeclaration -> {
                    RfReifierExpression.reify(it.primaryProperty.expression, symbolTable)
                    val typeReified = it.primaryProperty.expression.getTypeReifiedNotNull()
                    if (typeReified.typeClass != INSTANCE)
                        throw LineException("property should be initialized", it.line)
                    it.primaryProperty.typeReified = typeReified
                    symbolTable.addProperty(it.primaryProperty)
                }
                is RfStatementExpression -> {
                    RfReifierExpression.reify(it.expression, symbolTable)
                }
            }
        }
    }
}