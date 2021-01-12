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

package verikc.rs.resolve

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.TYPE_UBIT
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverPassType: RsResolverPassBase() {

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        type.parameterProperties.forEach { resolveParameterProperty(it, type.symbol, symbolTable) }

        // TODO general handling of parent generic type
        type.typeParent.typeGenerified = symbolTable.resolveTypeSymbol(
            type.typeParent.typeIdentifier,
            scopeSymbol,
            type.line
        ).toTypeGenerified()

        type.typeObject.typeGenerified = type.symbol.toTypeGenerified()
        symbolTable.addProperty(type.typeObject, scopeSymbol)

        resolveTypeFunction(type.typeConstructorFunction, type.symbol, scopeSymbol, symbolTable)
        if (type.enumConstructorFunction != null) {
            resolveTypeFunction(type.enumConstructorFunction, type.symbol, scopeSymbol, symbolTable)
        }
    }

    private fun resolveTypeFunction(
        function: RsFunction,
        typeSymbol: Symbol,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        function.parameterProperties.forEach { resolveParameterProperty(it, function.symbol, symbolTable) }
        function.returnTypeGenerified = typeSymbol.toTypeGenerified()
        symbolTable.addFunction(function, scopeSymbol)
    }

    private fun resolveParameterProperty(
        parameterProperty: RsProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (parameterProperty.expression != null) {
            RsResolverExpression.resolve(parameterProperty.expression, scopeSymbol, symbolTable)
        }
        parameterProperty.typeGenerified = if (parameterProperty.typeIdentifier == "_ubit") {
            TYPE_UBIT.toTypeGenerified(0)
        } else throw LineException("type parameter not supported", parameterProperty.line)
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }
}
