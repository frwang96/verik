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

package verikc.rs.resolve

import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rs.ast.RsCompilationUnit
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

object RsResolverType: RsResolverBase() {

    override fun resolve(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        ResolverIndexer.resolve(compilationUnit, symbolTable)
        super.resolve(compilationUnit, symbolTable)
    }

    override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
        val typeParent = type.typeParent
        typeParent.typeSymbol = symbolTable.resolveType(
            typeParent.typeIdentifier,
            scopeSymbol,
            type.line
        )

        symbolTable.addScope(type.symbol, scopeSymbol, type.line)
        type.parameterProperties.forEach {
            resolveParameterProperty(it, type.symbol, symbolTable)
        }
    }

    private fun resolveParameterProperty(
        parameterProperty: RsProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {
        if (parameterProperty.typeIdentifier == null)
            throw LineException("parameter property type identifier expected", parameterProperty.line)
        parameterProperty.typeSymbol = symbolTable.resolveType(
            parameterProperty.typeIdentifier,
            scopeSymbol,
            parameterProperty.line
        )
        symbolTable.addProperty(parameterProperty, scopeSymbol)
    }

    private object ResolverIndexer: RsResolverBase() {

        override fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {
            symbolTable.addType(type, scopeSymbol)
        }
    }
}
