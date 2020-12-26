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
import verikc.rf.RfReifyUtil

object PsPassUtil {

    fun passCompilationUnit(string: String): PsCompilationUnit {
        val compilationUnit = PsStageDriver.build(RfReifyUtil.reifyCompilationUnit(string))
        PsStageDriver.pass(compilationUnit)
        return compilationUnit
    }

    fun passComponentInstance(fileContext: String, moduleContext: String, string: String): PsComponentInstance {
        val moduleString = """
            class _m: _module {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = passModule(fileContext, moduleString)
        if (module.componentInstances.size != 1) {
            throw IllegalArgumentException("${module.componentInstances.size} component instances found")
        }
        return module.componentInstances[0]
    }

    fun passActionBlock(fileContext: String, string: String): PsActionBlock {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = passModule(fileContext, moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    private fun passFile(string: String): PsFile {
        return passCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun passDeclaration(fileContext: String, string: String): PsDeclaration {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = passFile(fileString)
        return file.declarations.last()
    }

    private fun passModule(fileContext: String, string: String): PsModule {
        return passDeclaration(fileContext, string) as PsModule
    }
}