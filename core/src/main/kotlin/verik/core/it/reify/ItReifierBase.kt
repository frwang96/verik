/*
 * Copyright 2020 Francis Wang
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

package verik.core.it.reify

import verik.core.it.*
import verik.core.it.symbol.ItSymbolTable

abstract class ItReifierBase {

    fun reifyFile(file: ItFile, symbolTable: ItSymbolTable) {
        file.declarations.forEach { reifyDeclaration(it, symbolTable) }
    }

    fun reifyDeclaration(declaration: ItDeclaration, symbolTable: ItSymbolTable) {
        when (declaration) {
            is ItModule -> reifyModule(declaration, symbolTable)
            is ItPort -> reifyPort(declaration, symbolTable)
            is ItPrimaryProperty -> reifyPrimaryProperty(declaration, symbolTable)
            is ItActionBlock -> reifyActionBlock(declaration, symbolTable)
        }
    }

    protected open fun reifyModule(module: ItModule, symbolTable: ItSymbolTable) {}

    protected open fun reifyPort(port: ItPort, symbolTable: ItSymbolTable) {}

    protected open fun reifyPrimaryProperty(primaryProperty: ItPrimaryProperty, symbolTable: ItSymbolTable) {}

    protected open fun reifyActionBlock(actionBlock: ItActionBlock, symbolTable: ItSymbolTable) {}
}