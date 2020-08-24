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

package verik.core.kt.symbol

import verik.core.base.Symbol
import verik.core.base.SymbolContext
import verik.core.kt.*

object KtSymbolTableBuilder {

    fun build(symbolContext: SymbolContext): KtSymbolTable {
        val symbolTable = KtSymbolTable()
        for (pkg in symbolContext.pkgs()) {
            symbolTable.addPkg(pkg)
            for (file in symbolContext.files(pkg)) {
                symbolTable.addFile(file)
            }
        }
        return symbolTable
    }

    fun buildFile(file: KtFile, symbolTable: KtSymbolTable) {
        file.declarations.forEach { buildDeclaration(it, file.file, symbolTable) }
    }

    private fun buildDeclaration(declaration: KtDeclaration, parent: Symbol, symbolTable: KtSymbolTable) {
        when (declaration) {
            is KtDeclarationType -> {
                symbolTable.addScope(declaration.symbol, parent, declaration.line)
                declaration.parameters.forEach { buildDeclaration(it, declaration.symbol, symbolTable) }
                declaration.enumEntries?.forEach { buildDeclaration(it, declaration.symbol, symbolTable) }
                declaration.declarations.forEach { buildDeclaration(it, declaration.symbol, symbolTable) }
            }
            is KtDeclarationFunction -> {
                symbolTable.addScope(declaration.symbol, parent, declaration.line)
                declaration.parameters.forEach { buildDeclaration(it, declaration.symbol, symbolTable) }
            }
            is KtDeclarationProperty -> {
                symbolTable.addProperty(declaration, parent, declaration.line)
            }
        }
    }
}