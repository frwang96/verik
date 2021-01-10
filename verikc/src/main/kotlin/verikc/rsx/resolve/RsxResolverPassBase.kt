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

package verikc.rsx.resolve

import verikc.base.symbol.Symbol
import verikc.rsx.ast.RsxCompilationUnit
import verikc.rsx.ast.RsxFunction
import verikc.rsx.ast.RsxProperty
import verikc.rsx.ast.RsxType
import verikc.rsx.table.RsxSymbolTable

abstract class RsxResolverPassBase {

    open fun resolve(compilationUnit: RsxCompilationUnit, symbolTable: RsxSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.types.forEach { resolveType(it, file.config.symbol, symbolTable) }
                file.functions.forEach { resolveFunction(it, file.config.symbol, symbolTable) }
                file.properties.forEach { resolveProperty(it, file.config.symbol, symbolTable) }
            }
        }
    }

    protected open fun resolveType(type: RsxType, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {}

    protected open fun resolveFunction(function: RsxFunction, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {}

    protected open fun resolveProperty(property: RsxProperty, scopeSymbol: Symbol, symbolTable: RsxSymbolTable) {}
}
