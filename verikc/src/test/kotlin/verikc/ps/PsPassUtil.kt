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

package verikc.ps

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ps.ast.*
import verikc.vk.VkBuildUtil

object PsPassUtil {

    fun passCompilationUnit(string: String): PsCompilationUnit {
        val compilationUnit = PsStageDriver.build(VkBuildUtil.buildCompilationUnit(string))
        PsStageDriver.pass(compilationUnit)
        return compilationUnit
    }

    fun passModuleActionBlock(fileContext: String, moduleContext: String, string: String): PsActionBlock {
        val moduleString = """
            class M : Module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = passComponent(fileContext, moduleString)
        return module.actionBlocks.last()
    }

    fun passModuleActionBlockExpression(fileContext: String, moduleContext: String, string: String): PsExpression {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = passModuleActionBlock(fileContext, moduleContext, actionBlockString)
        return actionBlock.block.expressions.last()
    }

    fun passModuleMethodBlock(fileContext: String, moduleContext: String, string: String): PsMethodBlock {
        val moduleString = """
            class M : Module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = passComponent(fileContext, moduleString)
        return module.methodBlocks.last()
    }

    private fun passFile(string: String): PsFile {
        return passCompilationUnit(string).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    private fun passComponent(fileContext: String, string: String): PsComponent {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = passFile(fileString)
        return file.components.last()
    }
}