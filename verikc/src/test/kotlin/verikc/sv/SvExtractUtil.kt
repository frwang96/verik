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

package verikc.sv

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ps.PsPassUtil
import verikc.sv.ast.*

object SvExtractUtil {

    fun extractFile(string: String): SvFile {
        val compilationUnit = SvStageDriver.extract(PsPassUtil.passCompilationUnit(string))
        return compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
    }

    fun extractModule(fileContext: String, string: String): SvModule {
        return extractComponentDeclaration(fileContext, string) as SvModule
    }

    fun extractModuleProperty(fileContext: String, string: String): SvProperty {
        val moduleString = """
            class _m: _module() {
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        return module.properties.last()
    }

    fun extractModuleComponentInstance(fileContext: String, moduleContext: String, string: String): SvComponentInstance {
        val moduleString = """
            class _m: _module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        return module.componentInstances.last()
    }

    fun extractModuleActionBlock(fileContext: String, moduleContext: String, string: String): SvActionBlock {
        val moduleString = """
            class _m: _module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        return module.actionBlocks.last()
    }

    fun extractModuleActionBlockExpression(fileContext: String, moduleContext: String, string: String): SvExpression {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = extractModuleActionBlock(fileContext, moduleContext, actionBlockString)
        return actionBlock.block.expressions.last()
    }

    fun extractModuleMethodBlock(fileContext: String, moduleContext: String, string: String): SvMethodBlock {
        val moduleString = """
            class _m: _module() {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        return module.methodBlocks.last()
    }

    fun extractEnum(fileContext: String, string: String): SvEnum {
        return extractPkgDeclaration(fileContext, string) as SvEnum
    }

    private fun extractComponentDeclaration(fileContext: String, string: String): SvDeclaration {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = extractFile(fileString)
        return file.componentDeclarations.last()
    }

    private fun extractPkgDeclaration(fileContext: String, string: String): SvDeclaration {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = extractFile(fileString)
        return file.pkgDeclarations.last()
    }
}