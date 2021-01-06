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

package verikc.ge.reify

import verikc.base.ast.LineException
import verikc.ge.ast.*
import verikc.ge.symbol.GeSymbolTable

abstract class GeReifierBase {

    fun reifyFile(file: GeFile, symbolTable: GeSymbolTable) {
        file.declarations.forEach { reifyDeclaration(it, symbolTable) }
    }

    protected open fun reifyModule(module: GeModule, symbolTable: GeSymbolTable) {}

    protected open fun reifyEnum(enum: GeEnum, symbolTable: GeSymbolTable) {}

    protected open fun reifyPrimaryProperty(primaryProperty: GePrimaryProperty, symbolTable: GeSymbolTable) {}

    private fun reifyDeclaration(declaration: GeDeclaration, symbolTable: GeSymbolTable) {
        when (declaration) {
            is GeModule -> reifyModule(declaration, symbolTable)
            is GeEnum -> reifyEnum(declaration, symbolTable)
            is GePrimaryProperty -> reifyPrimaryProperty(declaration, symbolTable)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}
