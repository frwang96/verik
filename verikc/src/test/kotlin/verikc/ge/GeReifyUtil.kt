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

package verikc.ge

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ge.ast.*
import verikc.vk.VkBuildUtil

object GeReifyUtil {

    fun reifyCompilationUnit(string: String): GeCompilationUnit {
        val compilationUnit = GeStageDriver.build(VkBuildUtil.buildCompilationUnit(string))
        GeStageDriver.reify(compilationUnit)
        return compilationUnit
    }

    fun reifyPort(string: String): GePort {
        val moduleString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val module = reifyModule(moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun reifyMethodBlock(moduleContext: String, string: String): GeMethodBlock {
        val moduleString = """
            class _m: _module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = reifyModule(moduleString)
        if (module.methodBlocks.size != 1) {
            throw IllegalArgumentException("${module.methodBlocks.size} method blocks found")
        }
        return module.methodBlocks[0]
    }

    fun reifyExpression(moduleContext: String, string: String): GeExpression {
        val statement = reifyStatement(moduleContext, string)
        return if (statement is GeStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    private fun reifyFile(string: String): GeFile {
        return reifyCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun reifyDeclaration(string: String): GeDeclaration {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = reifyFile(fileString)
        return file.declarations.last()
    }

    private fun reifyModule(string: String): GeModule {
        return reifyDeclaration(string) as GeModule
    }

    private fun reifyActionBlock(moduleContext: String, string: String): GeActionBlock {
        val moduleString = """
            class _m: _module() {
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

    private fun reifyStatement(moduleContext: String, string: String): GeStatement {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = reifyActionBlock(moduleContext, actionBlockString)
        return actionBlock.block.statements.last()
    }
}