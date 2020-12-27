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

package verikc.sv

import verikc.FILE_SYMBOL
import verikc.PKG_SYMBOL
import verikc.ps.PsPassUtil
import verikc.sv.ast.*
import verikc.sv.build.SvBuildable

object SvExtractUtil {

    fun extractModuleFile(string: String): SvFile {
        val compilationUnit = SvStageDriver.extract(PsPassUtil.passCompilationUnit(string))
        return compilationUnit.pkg(PKG_SYMBOL).moduleFile(FILE_SYMBOL)
    }

    fun extractModule(fileContext: String, string: String): SvModule {
        return extractModuleDeclaration(fileContext, string) as SvModule
    }

    fun extractPort(fileContext: String, string: String): SvPort {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun extractPrimaryProperty(fileContext: String, string: String): SvPrimaryProperty {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        if (module.primaryProperties.size != 1) {
            throw IllegalArgumentException("${module.primaryProperties.size} primary properties found")
        }
        return module.primaryProperties[0]
    }

    fun extractComponentInstance(fileContext: String, moduleContext: String, string: String): SvComponentInstance {
        val moduleString = """
            class _m: _module {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        if (module.componentInstances.size != 1) {
            throw IllegalArgumentException("${module.componentInstances.size} component instances found")
        }
        return module.componentInstances[0]
    }

    fun extractActionBlock(fileContext: String, moduleContext: String, string: String): SvActionBlock {
        val moduleString = """
            class _m: _module {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(fileContext, moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    fun extractExpression(fileContext: String, moduleContext: String, string: String): SvExpression {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = extractActionBlock(fileContext, moduleContext, actionBlockString)
        val statement = actionBlock.block.statements.last()
        return if (statement is SvStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    fun extractPkgFile(string: String): SvFile {
        val compilationUnit = SvStageDriver.extract(PsPassUtil.passCompilationUnit(string))
        return compilationUnit.pkg(PKG_SYMBOL).pkgFile(FILE_SYMBOL)
    }

    fun extractEnum(fileContext: String, string: String): SvEnum {
        return extractPkgDeclaration(fileContext, string) as SvEnum
    }

    private fun extractModuleDeclaration(fileContext: String, string: String): SvBuildable {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = extractModuleFile(fileString)
        return file.declarations.last()
    }

    private fun extractPkgDeclaration(fileContext: String, string: String): SvBuildable {
        val fileString = """
            package test
            $fileContext
            $string
        """.trimIndent()
        val file = extractPkgFile(fileString)
        return file.declarations.last()
    }
}