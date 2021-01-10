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

package verikc.rsx.resolve

import verikc.base.symbol.Symbol
import verikc.rsx.ast.RsxCompilationUnit
import verikc.rsx.ast.RsxType
import verikc.rsx.table.RsxSymbolTable

object RsxResolverPassType: RsxResolverPassBase() {

    override fun resolve(compilationUnit: RsxCompilationUnit, symbolTable: RsxSymbolTable) {
        Indexer.resolve(compilationUnit, symbolTable)
        super.resolve(compilationUnit, symbolTable)
    }

    override fun resolveType(type: RsxType, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
        // TODO general handling of parent generic type
        type.typeParent.typeGenerified = symbolTable.resolveTypeSymbol(
            type.typeParent.typeIdentifier,
            scopeSymbol,
            type.line
        ).toTypeGenerified()
        symbolTable.addScope(type.symbol, scopeSymbol, type.line)
    }

    private object Indexer: RsxResolverPassBase() {

        override fun resolveType(type: RsxType, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {
            symbolTable.addType(type, scopeSymbol)
        }
    }
}
