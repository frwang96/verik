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
import verikc.rs.ast.RsCompilationUnit
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverPassType: RsResolverPassBase() {

    override fun resolve(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        Indexer.resolve(compilationUnit, symbolTable)
        super.resolve(compilationUnit, symbolTable)
    }

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        symbolTable.addScope(type.symbol, scopeSymbol, type.line)

        // TODO general handling of parent generic type
        type.typeParent.typeGenerified = symbolTable.resolveTypeSymbol(
            type.typeParent.typeIdentifier,
            scopeSymbol,
            type.line
        ).toTypeGenerified()

        type.parameterProperties.forEach { resolveParameterProperty(it, type.symbol, symbolTable) }
    }

    private fun resolveParameterProperty(
        parameterProperty: RsProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (parameterProperty.typeIdentifier == null)
            throw LineException("parameter property type identifier expected", parameterProperty.line)

        parameterProperty.typeGenerified = if (parameterProperty.expression != null) {
            RsResolverExpression.resolve(parameterProperty.expression, scopeSymbol, symbolTable)
            parameterProperty.expression.getTypeGenerifiedNotNull()
        } else {
            // TODO general handling of type parameters
            symbolTable.resolveTypeSymbol(
                parameterProperty.typeIdentifier,
                scopeSymbol,
                parameterProperty.line
            ).toTypeGenerified()
        }
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }

    private object Indexer: RsResolverPassBase() {

        override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
            symbolTable.addType(type, scopeSymbol)
        }
    }
}
