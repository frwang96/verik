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

import verikc.base.symbol.Symbol
import verikc.rs.ast.RsCompilationUnit
import verikc.rs.ast.RsFunction
import verikc.rs.ast.RsProperty
import verikc.rs.ast.RsType
import verikc.rs.table.RsSymbolTable

abstract class RsPassBase {

    open fun pass(compilationUnit: RsCompilationUnit, symbolTable: RsSymbolTable) {
        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.types.forEach { passType(it, file.config.symbol, symbolTable) }
                file.functions.forEach { passFunction(it, file.config.symbol, symbolTable) }
                file.properties.forEach { passProperty(it, file.config.symbol, symbolTable) }
            }
        }
    }

    protected open fun passType(type: RsType, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}

    protected open fun passFunction(function: RsFunction, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}

    protected open fun passProperty(property: RsProperty, scopeSymbol: Symbol, symbolTable: RsSymbolTable) {}
}
