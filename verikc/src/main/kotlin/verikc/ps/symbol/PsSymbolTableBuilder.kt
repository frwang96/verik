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

package verikc.ps.symbol

import verikc.base.ast.LineException
import verikc.ps.ast.*

object PsSymbolTableBuilder {

    fun buildPkg(pkg: PsPkg, symbolTable: PsSymbolTable) {
        symbolTable.addPkg(pkg)
        pkg.files.forEach { buildFile(it, symbolTable) }
    }

    fun buildFile(file: PsFile, symbolTable: PsSymbolTable) {
        symbolTable.addFile(file)
        file.declarations.forEach { buildDeclaration(it, symbolTable) }
    }

    fun buildDeclaration(declaration: PsDeclaration, symbolTable: PsSymbolTable) {
        when (declaration) {
            is PsModule -> {
                declaration.ports.forEach { buildDeclaration(it, symbolTable) }
                declaration.primaryProperties.forEach { buildDeclaration(it, symbolTable) }
                symbolTable.addType(declaration)
            }
            is PsEnum -> {
                symbolTable.addType(declaration)
                declaration.properties.forEach { symbolTable.addProperty(declaration, it) }
            }
            is PsPort -> symbolTable.addProperty(declaration)
            is PsPrimaryProperty -> symbolTable.addProperty(declaration)
            else -> {
                throw LineException("declaration type not supported", declaration.line)
            }
        }
    }
}
