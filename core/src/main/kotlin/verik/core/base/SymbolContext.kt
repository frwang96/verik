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

package verik.core.base

import verik.core.main.config.FileConfig
import verik.core.main.config.PkgConfig
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

    fun registerSymbol(file: Symbol, identifier: String): Symbol {
        if (!file.isFileSymbol()) throw IllegalArgumentException("file expected but got $file")
        val fileContext = getFileContext(file)
        fileContext.count += 1
        val symbol = Symbol(file.pkg, file.file, fileContext.count)
        identifierMap[symbol] = identifier
        return symbol
    }

    fun identifier(symbol: Symbol): String? {
        return identifierMap[symbol]
    }

    fun pkgs(): List<Symbol> {
        return pkgs.map { it.symbol }
    }

    fun files(pkg: Symbol): List<Symbol> {
        if (!pkg.isPkgSymbol()) throw IllegalArgumentException("pkg expected but got $pkg")
        val pkgContext = getPkgContext(pkg)
        return pkgContext.files.map { it.symbol }
    }

    fun countPkgs(): Int {
        return pkgs.size
    }

    fun countFiles(): Int {
        return pkgs.fold(0) { sum, pkgContext -> sum + pkgContext.files.size }
    }

    fun pkgConfig(pkg: Symbol): PkgConfig {
        return getPkgContext(pkg).config
    }

    fun fileConfig(file: Symbol): FileConfig {
        return getFileContext(file).config
    }

    fun matchPkg(identifier: String): Symbol? {
        return pkgIdentifierMap[identifier]
    }

    private fun getPkgContext(pkg: Symbol): PkgContext {
        return if (pkg.pkg > 0 && pkg.pkg <= pkgs.size) {
            pkgs[pkg.pkg - 1]
        } else throw IllegalArgumentException("package ${pkg.toPkgSymbol()} has not been defined")
    }

    private fun getFileContext(file: Symbol): FileContext {
        val pkgContext = getPkgContext(file)
        return if (file.file > 0 && file.file <= pkgContext.files.size) {
            pkgContext.files[file.file - 1]
        } else throw IllegalArgumentException("file ${file.toFileSymbol()} has not been defined")
    }
}