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

package verik.core.rf.symbol

import verik.core.base.LineException
import verik.core.rf.*


object RfSymbolTableBuilder {

    fun buildFile(file: RfFile, symbolTable: RfSymbolTable) {
        file.declarations.forEach { buildDeclaration(it, symbolTable) }
    }

    fun buildDeclaration(declaration: RfDeclaration, symbolTable: RfSymbolTable) {
        when (declaration) {
            is RfModule -> {
                declaration.ports.forEach { buildDeclaration(it, symbolTable) }
                declaration.primaryProperties.forEach { buildDeclaration(it, symbolTable) }
                symbolTable.addComponent(declaration)
            }
            is RfEnum -> {
                // TODO build symbol table for enum
            }
            is RfPort -> symbolTable.addProperty(declaration)
            is RfPrimaryProperty -> symbolTable.addProperty(declaration)
            else -> {
                throw LineException("declaration type not supported", declaration)
            }
        }
    }
}