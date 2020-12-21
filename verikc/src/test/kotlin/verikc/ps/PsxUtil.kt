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
import verikc.sv.ast.*
import verikc.sv.build.SvBuildable

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

    fun extractModuleFile(string: String): SvFile {
        val symbolTable = PsSymbolTable()
        val compilationUnit = passCompilationUnit(string, symbolTable)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.extractModuleFile(symbolTable)!!
    }

    fun extractModule(string: String): SvModule {
        return extractModuleDeclaration(string) as SvModule
    }

    fun extractPort(string: String): SvPort {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = extractModule(moduleString)
        if (module.ports.size != 1) {
            throw IllegalArgumentException("${module.ports.size} ports found")
        }
        return module.ports[0]
    }

    fun extractPrimaryProperty(string: String): SvPrimaryProperty {
        val moduleString = """
            class _m: _module {
                $string
            }
        """.trimIndent()
        val module = extractModule(moduleString)
        if (module.primaryProperties.size != 1) {
            throw IllegalArgumentException("${module.primaryProperties.size} primary properties found")
        }
        return module.primaryProperties[0]
    }

    fun extractActionBlock(moduleContext: String, string: String): SvActionBlock {
        val moduleString = """
            class _m: _module {
                $moduleContext
                $string
            }
        """.trimIndent()
        val module = extractModule(moduleString)
        if (module.actionBlocks.size != 1) {
            throw IllegalArgumentException("${module.actionBlocks.size} action blocks found")
        }
        return module.actionBlocks[0]
    }

    fun extractExpression(moduleContext: String, string: String): SvExpression {
        val actionBlockString = """
            @run fun f() {
                $string
            }
        """.trimIndent()
        val actionBlock = extractActionBlock(moduleContext, actionBlockString)
        val statement = actionBlock.block.statements.last()
        return if (statement is SvStatementExpression) {
            statement.expression
        } else throw IllegalArgumentException("expression statement expected")
    }

    fun extractPkgFile(string: String): SvFile {
        val symbolTable = PsSymbolTable()
        val compilationUnit = passCompilationUnit(string, symbolTable)
        val file = compilationUnit.pkg(PKG_SYMBOL).file(FILE_SYMBOL)
        return file.extractPkgFile()!!
    }

    fun extractEnum(string: String): SvEnum {
        return extractPkgDeclaration(string) as SvEnum
    }

    private fun passCompilationUnit(string: String, symbolTable: PsSymbolTable): PsCompilationUnit {
        val compilationUnit = PsDriver.build(RfxUtil.reifyCompilationUnit(string))
        PsDriver.pass(compilationUnit, symbolTable)
        return compilationUnit
    }

    private fun passFile(string: String): PsFile {
        val symbolTable = PsSymbolTable()
        return passCompilationUnit(string, symbolTable).pkg(PKG_SYMBOL).file(FILE_SYMBOL)
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

    private fun extractModuleDeclaration(string: String): SvBuildable {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = extractModuleFile(fileString)
        return file.declarations.last()
    }

    private fun extractPkgDeclaration(string: String): SvBuildable {
        val fileString = """
            package test
            $string
        """.trimIndent()
        val file = extractPkgFile(fileString)
        return file.declarations.last()
    }
}