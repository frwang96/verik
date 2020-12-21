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

package verikc.ps

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ps.ast.*
import verikc.ps.symbol.PsSymbolTable
import verikc.rf.RfxUtil

object PsxUtil {

    fun passActionBlock(string: String): PsActionBlock {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = passModule(moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    private fun passCompilationUnit(string: String): PsCompilationUnit {
        val compilationUnit = PsDriver.build(RfxUtil.reifyCompilationUnit(string))
        PsDriver.pass(compilationUnit, PsSymbolTable())
        return compilationUnit
    }

    private fun passFile(string: String): PsFile {
        return passCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun passDeclaration(string: String): PsDeclaration {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = passFile(fileString)
        return file.declarations.last()
    }

    private fun passModule(string: String): PsModule {
        return passDeclaration(string) as PsModule
    }
}