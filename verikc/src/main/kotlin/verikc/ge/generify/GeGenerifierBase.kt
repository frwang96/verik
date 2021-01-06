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

package verikc.ge.generify

import verikc.base.ast.LineException
import verikc.ge.ast.*
import verikc.ge.symbol.GeSymbolTable

abstract class GeGenerifierBase {

    fun generifyFile(file: GeFile, symbolTable: GeSymbolTable) {
        file.declarations.forEach { generifyDeclaration(it, symbolTable) }
    }

    protected open fun generifyModule(module: GeModule, symbolTable: GeSymbolTable) {}

    protected open fun generifyEnum(enum: GeEnum, symbolTable: GeSymbolTable) {}

    protected open fun generifyPrimaryProperty(primaryProperty: GePrimaryProperty, symbolTable: GeSymbolTable) {}

    private fun generifyDeclaration(declaration: GeDeclaration, symbolTable: GeSymbolTable) {
        when (declaration) {
            is GeModule -> generifyModule(declaration, symbolTable)
            is GeEnum -> generifyEnum(declaration, symbolTable)
            is GePrimaryProperty -> generifyPrimaryProperty(declaration, symbolTable)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}
