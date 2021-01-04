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

package verikc.rf.reify

import verikc.base.ast.LineException
import verikc.rf.ast.*
import verikc.rf.symbol.RfSymbolTable

abstract class RfReifierBase {

    fun reifyFile(file: RfFile, symbolTable: RfSymbolTable) {
        file.declarations.forEach { reifyDeclaration(it, symbolTable) }
    }

    protected open fun reifyModule(module: RfModule, symbolTable: RfSymbolTable) {}

    protected open fun reifyEnum(enum: RfEnum, symbolTable: RfSymbolTable) {}

    protected open fun reifyPort(port: RfPort, symbolTable: RfSymbolTable) {}

    protected open fun reifyPrimaryProperty(primaryProperty: RfPrimaryProperty, symbolTable: RfSymbolTable) {}

    protected open fun reifyComponentInstance(componentInstance: RfComponentInstance, symbolTable: RfSymbolTable) {}

    protected open fun reifyActionBlock(actionBlock: RfActionBlock, symbolTable: RfSymbolTable) {}

    private fun reifyDeclaration(declaration: RfDeclaration, symbolTable: RfSymbolTable) {
        when (declaration) {
            is RfModule -> reifyModule(declaration, symbolTable)
            is RfEnum -> reifyEnum(declaration, symbolTable)
            is RfPort -> reifyPort(declaration, symbolTable)
            is RfPrimaryProperty -> reifyPrimaryProperty(declaration, symbolTable)
            is RfComponentInstance -> reifyComponentInstance(declaration, symbolTable)
            is RfActionBlock -> reifyActionBlock(declaration, symbolTable)
            else -> throw LineException("declaration type not supported", declaration.line)
        }
    }
}
