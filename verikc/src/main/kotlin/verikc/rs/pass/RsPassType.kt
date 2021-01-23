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

import verikc.base.ast.ExpressionClass
import verikc.base.ast.ExpressionClass.TYPE
import verikc.base.ast.ExpressionClass.VALUE
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsPassType: RsPassBase() {

    override fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        if (type.parameterProperties.isNotEmpty())
            throw LineException("type parameter not supported", type.line)

        // TODO general handling of parent generic type
        type.typeParent.typeGenerified = symbolTable.resolveTypeSymbol(
            type.typeParent.typeIdentifier,
            scopeSymbol,
            type.line
        ).toTypeGenerified()

        type.typeObject.typeGenerified = type.symbol.toTypeGenerified()
        symbolTable.setProperty(type.typeObject)

        if (type.topObject != null) {
            type.topObject.typeGenerified = type.symbol.toTypeGenerified()
            symbolTable.setProperty(type.topObject)
        }

        passTypeFunction(type.typeConstructorFunction, TYPE, type.symbol, scopeSymbol, symbolTable)
        if (type.instanceConstructorFunction != null) {
            passTypeFunction(type.instanceConstructorFunction, VALUE, type.symbol, scopeSymbol, symbolTable)
        }
    }

    private fun passTypeFunction(
        function: RsFunction,
        expressionClass: ExpressionClass,
        typeSymbol: Symbol,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (function.parameterProperties.isNotEmpty())
            throw LineException("type parameter not supported", function.line)
        function.returnTypeGenerified = typeSymbol.toTypeGenerified()
        symbolTable.addFunction(function, expressionClass, scopeSymbol)
    }
}
