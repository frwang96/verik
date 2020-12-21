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

package verikc.rf

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.rf.ast.*
import verikc.vk.VkUtil

object RfUtil {

    fun reifyCompilationUnit(string: String): RfCompilationUnit {
        return RfDriver.drive(VkUtil.buildCompilationUnit(string))
    }

    fun reifyPort(string: String): RfPort {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = reifyModule(moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun reifyExpression(moduleContext: String, string: String): RfExpression {
        val statement = reifyStatement(moduleContext, string)
        return if (statement is RfStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    private fun reifyFile(string: String): RfFile {
        return reifyCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun reifyDeclaration(string: String): RfDeclaration {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = reifyFile(fileString)
        return file.declarations.last()
    }

    private fun reifyModule(string: String): RfModule {
        return reifyDeclaration(string) as RfModule
    }

    private fun reifyActionBlock(moduleContext: String, string: String): RfActionBlock {
        val moduleString = """
            class _m: _module {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = reifyModule(moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    private fun reifyStatement(moduleContext: String, string: String): RfStatement {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = reifyActionBlock(moduleContext, actionBlockString)
        return actionBlock.block.statements.last()
    }
}