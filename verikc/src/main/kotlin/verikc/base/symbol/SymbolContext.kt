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

package verikc.base.symbol

import verikc.base.config.FileConfig
import verikc.base.config.PkgConfig
import verikc.lang.Lang
import java.util.concurrent.ConcurrentHashMap

class SymbolContext {

    private data class PkgContext(
        val symbol: Symbol,
        val config: PkgConfig,
        val fileContexts: List<FileContext>
    )

    private data class FileContext(
        val symbol: Symbol,
        val config: FileConfig
    )

    private var symbolCount = 1

    private val pkgContexts = ArrayList<PkgContext>()
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
        val pkgSymbol = registerSymbol(pkgConfig.identifierKt)
        val fileContexts = ArrayList<FileContext>()
        for (fileConfig in fileConfigs) {
            val fileSymbol = registerSymbol(fileConfig.identifier)
            fileContexts.add(FileContext(fileSymbol, fileConfig))
        }
        pkgContexts.add(PkgContext(pkgSymbol, pkgConfig, fileContexts))

        if (pkgConfig.identifierKt == "") throw IllegalArgumentException("use of the root package is prohibited")
        if (pkgIdentifierMap.contains(pkgConfig.identifierKt)) {
            throw IllegalArgumentException("package ${pkgConfig.identifierKt} has already been defined")
        } else {
            pkgIdentifierMap[pkgConfig.identifierKt] = pkgSymbol
        }
    }

    @Synchronized
    fun registerSymbol(identifier: String): Symbol {
        val symbol = Symbol(symbolCount)
        symbolCount += 1
        identifierMap[symbol] = identifier
        return symbol
    }

    fun identifier(symbol: Symbol): String? {
        return identifierMap[symbol]
    }

    fun pkgs(): List<Symbol> {
        return pkgContexts.map { it.symbol }
    }

    fun files(pkgSymbol: Symbol): List<Symbol> {
        val pkgContext = getPkgContext(pkgSymbol)
        return pkgContext.fileContexts.map { it.symbol }
    }

    fun countPkgs(): Int {
        return pkgContexts.size
    }

    fun countFiles(): Int {
        return pkgContexts.fold(0) { sum, pkgContext -> sum + pkgContext.fileContexts.size }
    }

    fun pkgConfig(pkgSymbol: Symbol): PkgConfig {
        return getPkgContext(pkgSymbol).config
    }

    fun fileConfig(fileSymbol: Symbol): FileConfig {
        return getFileContext(fileSymbol).config
    }

    private fun getPkgContext(pkgSymbol: Symbol): PkgContext {
        return pkgContexts.find { it.symbol == pkgSymbol }
            ?: throw IllegalArgumentException("package $pkgSymbol has not been defined")
    }

    private fun getFileContext(fileSymbol: Symbol): FileContext {
        for (pkgContext in pkgContexts) {
            val fileContext = pkgContext.fileContexts.find { it.symbol == fileSymbol }
            if (fileContext != null) return fileContext
        }
        throw IllegalArgumentException("file $fileSymbol has not been defined")
    }
}
