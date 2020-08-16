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

package verik.core.main.symbol

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

    fun registerConfigs(pkgConfig: PkgConfig, fileConfigs: List<FileConfig>) {
        val files = ArrayList<FileContext>()
        for (fileConfig in fileConfigs) {
            val fileSymbol = Symbol(pkgs.size + 1, files.size + 1, 0)
            files.add(FileContext(fileSymbol, fileConfig, 0))
        }
        val pkgSymbol = Symbol(pkgs.size + 1, 0, 0)
        pkgs.add(PkgContext(pkgSymbol, pkgConfig, files))

        if (pkgConfig.pkgKt == "") throw IllegalArgumentException("use of the root package is prohibited")
        if (pkgIdentifierMap.contains(pkgConfig.pkgKt)) {
            throw IllegalArgumentException("package ${pkgConfig.pkgKt} has already been defined")
        } else {
            pkgIdentifierMap[pkgConfig.pkgKt] = pkgSymbol
        }
    }

    fun nextSymbol(file: Symbol): Symbol {
        if (!file.isFileSymbol()) throw IllegalArgumentException("file symbol expected but got $file")
        val fileContext = getFileContext(file)
        fileContext.count += 1
        return Symbol(file.pkg, file.file, fileContext.count)
    }

    fun pkgs(): List<Symbol> {
        return pkgs.map { it.symbol }
    }

    fun files(pkg: Symbol): List<Symbol> {
        if (!pkg.isPkgSymbol()) throw IllegalArgumentException("pkg symbol expected but got $pkg")
        val pkgContext = getPkgContext(pkg)
        return pkgContext.files.map { it.symbol }
    }

    fun countPkgs(): Int {
        return pkgs.size
    }

    fun countFiles(): Int {
        return pkgs.fold(0) { sum, pkgContext -> sum + pkgContext.files.size }
    }

    fun countSymbols(): Int {
        return pkgs.fold(1) { sumPkgs, pkgContext ->
            sumPkgs + pkgContext.files.fold(1) { sumFiles, fileContext ->
                sumFiles + fileContext.count
            }
        }
    }

    fun pkgConfig(pkg: Symbol): PkgConfig {
        return getPkgContext(pkg).config
    }

    fun fileConfig(file: Symbol): FileConfig {
        return getFileContext(file).config
    }

    fun pkg(identifier: String): Symbol? {
        return pkgIdentifierMap[identifier]
    }

    private fun getPkgContext(pkg: Symbol): PkgContext {
        return if (pkg.pkg > 0 && pkg.pkg <= pkgs.size) {
            pkgs[pkg.pkg - 1]
        } else throw IllegalArgumentException("symbol $pkg has not been defined")
    }

    private fun getFileContext(file: Symbol): FileContext {
        val pkgContext = getPkgContext(file)
        return if (file.file > 0 && file.file <= pkgContext.files.size) {
            pkgContext.files[file.file - 1]
        } else throw IllegalArgumentException("symbol $file has not been defined")
    }
}