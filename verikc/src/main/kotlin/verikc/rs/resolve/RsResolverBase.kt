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

import verikc.base.symbol.Symbol
import verikc.rs.ast.RsCompilationUnit
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

abstract class RsResolverBase {

    open fun resolve(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.types.forEach { resolveType(it, file.config.symbol, symbolTable) }
                file.functions.forEach { resolveFunction(it, file.config.symbol, symbolTable) }
                file.properties.forEach { resolveProperty(it, file.config.symbol, symbolTable) }
            }
        }
    }

    protected open fun resolveType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}

    protected open fun resolveFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}

    protected open fun resolveProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}
}
