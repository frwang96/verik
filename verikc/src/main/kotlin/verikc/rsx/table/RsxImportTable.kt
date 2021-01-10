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

package verikc.rsx.table

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.rsx.ast.RsxDeclaration
import verikc.rsx.ast.RsxPkg
import verikc.rsx.ast.RsxType

class RsxImportTable {

    private val pkgs = ArrayList<PkgEntry>()

    fun addPkg(pkg: RsxPkg) {
        if (pkgs.any { it.symbol == pkg.config.symbol })
            throw LineException("package ${pkg.config.symbol} has already been registered", Line(0))

        val declarationEntries = ArrayList<DeclarationEntry>()
        pkg.files.forEach { file ->
            file.types.forEach { addDeclarationEntries(it, declarationEntries) }
            file.functions.forEach { addDeclarationEntries(it, declarationEntries) }
            file.properties.forEach { addDeclarationEntries(it, declarationEntries) }
        }

        pkgs.add(
            PkgEntry(
                pkg.config.symbol,
                pkg.config.identifierKt,
                pkg.config.fileConfigs.map { it.symbol },
                declarationEntries
            )
        )
    }

    fun resolvePkg(pkgIdentifier: String, line: Line): Symbol {
        val pkgEntry = pkgs.find { it.identifier == pkgIdentifier }
            ?: throw LineException("could not resolve package $pkgIdentifier", line)
        return pkgEntry.symbol
    }

    fun resolveDeclarations(
        pkgIdentifier: String,
        declarationIdentifier: String,
        line: Line
    ): Pair<Symbol, List<Symbol>> {
        val pkgEntry = pkgs.find { it.identifier == pkgIdentifier }
            ?: throw LineException("could not resolve package $pkgIdentifier", line)
        val declarationEntries = pkgEntry.declarationEntries
            .filter { it.identifier == declarationIdentifier }
            .map { it.symbol }
        if (declarationEntries.isEmpty())
            throw LineException("could not resolve declaration $pkgIdentifier.$declarationIdentifier", line)
        return Pair(pkgEntry.symbol, declarationEntries)
    }

    fun getFileSymbols(pkgSymbol: Symbol, line: Line): List<Symbol> {
        val pkgEntry = pkgs.find { it.symbol == pkgSymbol }
            ?: throw LineException("package $pkgSymbol has not been registered", line)
        return pkgEntry.fileSymbols
    }

    private fun addDeclarationEntries(declaration: RsxDeclaration, declarationEntries: ArrayList<DeclarationEntry>) {
        declarationEntries.add(DeclarationEntry(declaration.symbol, declaration.identifier))
        if (declaration is RsxType) {
            declarationEntries.add(
                DeclarationEntry(
                    declaration.typeConstructorFunction.symbol,
                    declaration.typeConstructorFunction.identifier
                )
            )
        }
    }

    private data class PkgEntry(
        val symbol: Symbol,
        val identifier: String,
        val fileSymbols: List<Symbol>,
        val declarationEntries: List<DeclarationEntry>
    )

    private data class DeclarationEntry(
        val symbol: Symbol,
        val identifier: String
    )
}