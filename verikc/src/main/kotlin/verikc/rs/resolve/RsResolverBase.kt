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
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFunction
import verikc.kt.ast.KtPrimaryProperty
import verikc.kt.ast.KtType
import verikc.rs.symbol.RsSymbolTable

abstract class RsResolverBase {

    open fun resolve(compilationUnit: KtCompilationUnit, symbolTable: RsSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.types.forEach { resolveType(it, file.config.symbol, symbolTable) }
                file.functions.forEach { resolveFunction(it, file.config.symbol, symbolTable) }
                file.properties.forEach {
                    if (it is KtPrimaryProperty) resolvePrimaryProperty(it, file.config.symbol, symbolTable)
                }
            }
        }
    }

    protected open fun resolveType(
        type: KtType,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {}

    protected open fun resolveFunction(
        function: KtFunction,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {}

    protected open fun resolvePrimaryProperty(
        primaryProperty: KtPrimaryProperty,
        scopeSymbol: Symbol,
        symbolTable: RsSymbolTable
    ) {}
}
