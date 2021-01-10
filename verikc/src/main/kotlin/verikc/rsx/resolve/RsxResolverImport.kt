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

package verikc.rsx.resolve

import verikc.base.ast.Line
import verikc.base.symbol.Symbol
import verikc.lang.LangSymbol.SCOPE_LANG
import verikc.rsx.ast.RsxCompilationUnit
import verikc.rsx.ast.RsxFile
import verikc.rsx.symbol.RsxResolutionEntry
import verikc.rsx.table.RsxImportTable

object RsxResolverImport {

    fun resolve(compilationUnit: RsxCompilationUnit) {
        val importTable = RsxImportTable()
        for (pkg in compilationUnit.pkgs) {
            importTable.addPkg(pkg)
        }

        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                file.resolutionEntries = listOf(getResolutionEntry(file, importTable))
            }
        }
    }

    private fun getResolutionEntry(file: RsxFile, importTable: RsxImportTable): RsxResolutionEntry {
        val scopeSet = getScopeSet(file, importTable)
        val declarationSet = HashSet<Symbol>()
        for (importEntry in file.importEntries) {
            if (importEntry.identifiers.last() != "*") {
                if (importEntry.identifiers.size > 1 && importEntry.identifiers[0] == "verik") continue
                val pkgIdentifier = importEntry.identifiers.dropLast(1).joinToString(separator = ".")
                val declarationIdentifier = importEntry.identifiers.last()
                val (pkgSymbol, declarationSymbols) = importTable.resolveDeclarations(
                    pkgIdentifier,
                    declarationIdentifier,
                    importEntry.line
                )
                if (pkgSymbol !in scopeSet) {
                    declarationSet.addAll(declarationSymbols)
                }
            }
        }
        return RsxResolutionEntry(scopeSet.toList(), declarationSet.toList())
    }

    private fun getScopeSet(file: RsxFile, importTable: RsxImportTable): Set<Symbol> {
        val scopeSet = HashSet<Symbol>()
        scopeSet.add(SCOPE_LANG)
        scopeSet.addAll(importTable.getFileSymbols(file.config.pkgSymbol, Line(file.config.symbol, 0)))
        for (importEntry in file.importEntries) {
            if (importEntry.identifiers.last() == "*") {
                if (importEntry.identifiers[0] == "verik") continue
                val pkgIdentifier = importEntry.identifiers.dropLast(1).joinToString(separator = ".")
                val pkgSymbol = importTable.resolvePkg(pkgIdentifier, importEntry.line)
                scopeSet.addAll(importTable.getFileSymbols(pkgSymbol, importEntry.line))
            }
        }
        return scopeSet
    }
}