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

package verikc.kt.symbol

import verikc.base.ast.Line
import verikc.base.ast.LineException
import verikc.base.config.PkgConfig
import verikc.base.symbol.Symbol

class KtImportTable {

    private val pkgs = ArrayList<PkgEntry>()

    fun addPkg(pkgConfig: PkgConfig) {
        if (pkgs.any { it.symbol == pkgConfig.symbol })
            throw LineException("package ${pkgConfig.symbol} has already been registered", Line(0))
        pkgs.add(
            PkgEntry(
                pkgConfig.symbol,
                pkgConfig.identifierKt,
                pkgConfig.fileConfigs.map { it.symbol }
            )
        )
    }

    fun resolvePkg(identifier: String, line: Line): Symbol {
        val pkgEntry = pkgs.find { it.identifier == identifier }
            ?: throw LineException("could not resolve package $identifier", line)
        return pkgEntry.symbol
    }

    fun getFileSymbols(pkgSymbol: Symbol, line: Line): List<Symbol> {
        val pkgEntry = pkgs.find { it.symbol == pkgSymbol }
            ?: throw LineException("package $pkgSymbol has not been registered", line)
        return pkgEntry.fileSymbols
    }

    private data class PkgEntry(
        val symbol: Symbol,
        val identifier: String,
        val fileSymbols: List<Symbol>
    )
}