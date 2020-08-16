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

package verik.core.it.symbol

import verik.core.it.ItDeclaration
import verik.core.it.ItFile
import verik.core.it.ItModule
import verik.core.it.ItPort
import verik.core.main.LineException


object ItSymbolTableBuilder {

    fun build(file: ItFile): ItSymbolTable {
        val symbolTable = ItSymbolTable()
        file.declarations.forEach { buildDeclaration(it, symbolTable) }
        return symbolTable
    }

    private fun buildDeclaration(declaration: ItDeclaration, symbolTable: ItSymbolTable) {
        when (declaration) {
            is ItModule -> {
                declaration.ports.forEach { buildDeclaration(it, symbolTable) }
            }
            is ItPort -> {
                symbolTable.addProperty(declaration)
            }
            else -> {
                throw LineException("declaration type not supported", declaration)
            }
        }
    }
}