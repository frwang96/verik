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

package verikc.kt.resolve

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.symbol.Symbol
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.symbol.KtImportTable
import verikc.kt.symbol.KtResolutionEntry
import verikc.lang.LangSymbol.SCOPE_LANG

object KtResolverImport {

    fun resolve(compilationUnit: KtCompilationUnit) {
        val importTable = KtImportTable()
        for (pkg in compilationUnit.pkgs) {
            importTable.addPkg(pkg.config)
        }

        for (pkg in compilationUnit.pkgs) {
            for (file in pkg.files) {
                val scopeSet = getScopeSet(file, importTable)
                file.resolutionEntries = listOf(KtResolutionEntry(scopeSet.toList()))
            }
        }
    }

    private fun getScopeSet(file: KtFile, importTable: KtImportTable): Set<Symbol> {
        val scopeSet = HashSet<Symbol>()
        scopeSet.add(SCOPE_LANG)
        scopeSet.addAll(importTable.getFileSymbols(file.config.pkgSymbol, Line(file.config.symbol, 0)))
        for (importEntry in file.importEntries) {
            if (importEntry.identifiers[0] == "verik") continue
            if (importEntry.identifiers.last() == "*") {
                val pkgIdentifier = importEntry.identifiers.dropLast(1).joinToString(separator = ".")
                val pkgSymbol = importTable.resolvePkg(pkgIdentifier, importEntry.line)
                scopeSet.addAll(importTable.getFileSymbols(pkgSymbol, importEntry.line))
            } else {
                throw LineException("import entry with identifier not supported", importEntry.line)
            }
        }
        return scopeSet
    }
}