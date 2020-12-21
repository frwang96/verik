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
import verikc.vk.VkxUtil

object RfxUtil {

    fun reifyExpression(string: String): RfExpression {
        val statement = reifyStatement(string)
        return if (statement is RfStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    private fun reifyCompilationUnit(string: String): RfCompilationUnit {
        return RfDriver.drive(VkxUtil.buildCompilationUnit(string))
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

    private fun reifyActionBlock(string: String): RfActionBlock {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = reifyModule(moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    private fun reifyStatement(string: String): RfStatement {
        val actionBlockString = """
            @com fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = reifyActionBlock(actionBlockString)
        return actionBlock.block.statements.last()
    }
}