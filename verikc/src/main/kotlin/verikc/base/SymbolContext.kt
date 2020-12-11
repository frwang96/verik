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

package verikc.base

import verikc.base.ast.Symbol
import verikc.lang.Lang
import verikc.main.config.FileConfig
import verikc.main.config.PkgConfig
import java.util.concurrent.ConcurrentHashMap

class SymbolContext {

    private data class PkgContext(
            val symbol: Symbol,
            val config: PkgConfig,
            val files: List<FileContext>
    )

    private data class FileContext(
            val symbol: Symbol,
            val config: FileConfig,
            var count: Int
    )

    private val pkgs = ArrayList<PkgContext>()
    private val pkgIdentifierMap = ConcurrentHashMap<String, Symbol>()
    private val identifierMap = ConcurrentHashMap<Symbol, String>()

    init {
        for (type in Lang.types) {
            identifierMap[type.symbol] = type.identifier
        }
        for (function in Lang.functions) {
            identifierMap[function.symbol] = function.identifier
        }
        for (operator in Lang.operators) {
            identifierMap[operator.symbol] = operator.identifier
        }
    }

    fun registerConfigs(pkgConfig: PkgConfig, fileConfigs: List<FileConfig>) {
        val files = ArrayList<FileContext>()
        for (fileConfig in fileConfigs) {
            val fileSymbol = Symbol(pkgs.size + 1, files.size + 1, 0)
            identifierMap[fileSymbol] = fileConfig.file.name
            files.add(FileContext(fileSymbol, fileConfig, 0))
        }
        val pkgSymbol = Symbol(pkgs.size + 1, 0, 0)
        identifierMap[pkgSymbol] = pkgConfig.pkgKt
        pkgs.add(PkgContext(pkgSymbol, pkgConfig, files))

        if (pkgConfig.pkgKt == "") throw IllegalArgumentException("use of the root package is prohibited")
        if (pkgIdentifierMap.contains(pkgConfig.pkgKt)) {
            throw IllegalArgumentException("package ${pkgConfig.pkgKt} has already been defined")
        } else {
            pkgIdentifierMap[pkgConfig.pkgKt] = pkgSymbol
        }
    }

    fun registerSymbol(fileSymbol: Symbol, identifier: String): Symbol {
        val fileContext = getFileContext(fileSymbol)
        fileContext.count += 1
        val symbol = Symbol(fileSymbol.pkg, fileSymbol.file, fileContext.count)
        identifierMap[symbol] = identifier
        return symbol
    }

    fun identifier(symbol: Symbol): String? {
        return identifierMap[symbol]
    }

    fun pkgs(): List<Symbol> {
        return pkgs.map { it.symbol }
    }

    fun files(pkgSymbol: Symbol): List<Symbol> {
        val pkgContext = getPkgContext(pkgSymbol)
        return pkgContext.files.map { it.symbol }
    }

    fun countPkgs(): Int {
        return pkgs.size
    }

    fun countFiles(): Int {
        return pkgs.fold(0) { sum, pkgContext -> sum + pkgContext.files.size }
    }

    fun pkgConfig(pkgSymbol: Symbol): PkgConfig {
        return getPkgContext(pkgSymbol).config
    }

    fun fileConfig(fileSymbol: Symbol): FileConfig {
        return getFileContext(fileSymbol).config
    }

    private fun getPkgContext(pkgSymbol: Symbol): PkgContext {
        return if (pkgSymbol.pkg > 0 && pkgSymbol.pkg <= pkgs.size) {
            pkgs[pkgSymbol.pkg - 1]
        } else throw IllegalArgumentException("package $pkgSymbol has not been defined")
    }

    private fun getFileContext(fileSymbol: Symbol): FileContext {
        val pkgContext = getPkgContext(fileSymbol)
        return if (fileSymbol.file > 0 && fileSymbol.file <= pkgContext.files.size) {
            pkgContext.files[fileSymbol.file - 1]
        } else throw IllegalArgumentException("file $fileSymbol has not been defined")
    }
}
