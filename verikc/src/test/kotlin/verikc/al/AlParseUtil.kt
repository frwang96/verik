/*
 * Copyright (c) 2020 Francis Wang
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

package verikc.al

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.al.ast.AlCompilationUnit
import verikc.al.ast.AlFile
import verikc.al.ast.AlPkg
import verikc.base.config.FileConfig
import verikc.base.config.PkgConfig
import java.io.File

object AlParseUtil {

    fun parseCompilationUnit(string: String): AlCompilationUnit {
        val file = AlFile(
            getFileConfig(),
            AlTreeParser.parseKotlinFile(FILE_SYMBOL, string)
        )
        val pkg = AlPkg(getPkgConfig(), listOf(file))
        return AlCompilationUnit(listOf(pkg))
    }

    private fun getFileConfig(): FileConfig {
        return FileConfig(
            "test/test.kt",
            File("test/test.txt"),
            File("test/test.kt"),
            File("test/test.sv"),
            File("test/test.svh"),
            FILE_SYMBOL,
            PKG_SYMBOL
        )
    }

    private fun getPkgConfig(): PkgConfig {
        return PkgConfig(
            "test",
            "test_pkg",
            File("test"),
            File("test"),
            File("test"),
            PKG_SYMBOL,
            listOf(getFileConfig())
        )
    }
}