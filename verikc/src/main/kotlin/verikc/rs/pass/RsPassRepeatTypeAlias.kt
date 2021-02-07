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

package verikc.rs.pass

import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsTypeAlias
import verikc.rs.table.RsResolveException
import verikc.rs.table.RsSymbolTable

class RsPassRepeatTypeAlias: RsPassRepeatBase() {

    override fun passTypeAlias(typeAlias: RsTypeAlias, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (typeAlias.typeGenerified == null) {
            try {
                RsPassExpression.pass(typeAlias.expression, scopeSymbol, symbolTable)
                if (typeAlias.expression.getExpressionClassNotNull() != TYPE)
                    throw LineException("type expression expected", typeAlias.expression.line)
                val typeGenerified = typeAlias.expression.getTypeGenerifiedNotNull()
                typeAlias.typeGenerified = typeGenerified
                symbolTable.setTypeAlias(typeAlias)
                typeAlias.typeConstructor.returnTypeGenerified = typeGenerified
                symbolTable.addFunction(typeAlias.typeConstructor, TYPE, scopeSymbol)
            } catch (exception: RsResolveException) {
                isResolved = false
                if (throwException) throw exception
            }
        }
    }
}