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

package verikc.kt

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.al.AlRuleParser
import verikc.base.config.FileConfig
import verikc.base.config.PkgConfig
import verikc.base.symbol.SymbolContext
import verikc.kt.ast.KtCompilationUnit
import verikc.kt.ast.KtFile
import verikc.kt.ast.KtPkg
import java.io.File

object KtxUtil {

    fun parseFile(string: String): KtFile {
        val compilationUnit = parseCompilationUnit(string)
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun getFileConfig(): FileConfig {
        return FileConfig(
            File("test/test.kt"),
            File("test/test.kt"),
            File("test/test.sv"),
            File("test/test.svh"),
            FILE_SYMBOL,
            PKG_SYMBOL
        )
    }

    private fun getPkgConfig(): PkgConfig {
        return PkgConfig(
            File("test"),
            File("test"),
            File("test"),
            "test",
            "test_pkg",
            PKG_SYMBOL,
            listOf(getFileConfig())
        )
    }

    private fun parseCompilationUnit(string: String): KtCompilationUnit {
        val symbolContext = SymbolContext()
        symbolContext.registerSymbol("test")
        symbolContext.registerSymbol("test/test.kt")
        val file = KtFile(
            AlRuleParser.parseKotlinFile(FILE_SYMBOL, string),
            getFileConfig(),
            symbolContext
        )
        val pkg = KtPkg(getPkgConfig(), listOf(file))
        return KtCompilationUnit(listOf(pkg))
    }
}